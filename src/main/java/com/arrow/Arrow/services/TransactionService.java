package com.arrow.Arrow.services;

import com.arrow.Arrow.ExceptionHandling.InsufficientBalanceException;
import com.arrow.Arrow.ExceptionHandling.UserNotFoundException;
import com.arrow.Arrow.dto.FetchTransactionsDTO;
import com.arrow.Arrow.dto.WithdrawDto;
import com.arrow.Arrow.entity.User;
import com.arrow.Arrow.entity.UserProfile;
import com.arrow.Arrow.repository.ProfileRepository;
import com.arrow.Arrow.repository.UserRepository;
import com.arrow.Arrow.util.TransStatus;
import com.arrow.Arrow.util.TransactionType;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.arrow.Arrow.entity.Transaction;
import com.arrow.Arrow.repository.TransactionsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    UserRepository userRepository;
    @Autowired
    TransactionsRepository transactionsRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    UserServiceImpl userService;

    @Transactional
    public Transaction depositByUsername(String username, double amount){
        User user=userRepository.findByUsername(username);
        if(user.getUsername()!=null){
            // Create deposit transaction ..
            Transaction depositTransaction = new Transaction();
            depositTransaction.setUser(user);
            depositTransaction.setTransactionType(TransactionType.DEPOSIT);
            depositTransaction.setAmount(amount);
            depositTransaction.setTransStatus(TransStatus.SUBMITTED);
            //depositTransaction.setTransactionTime(LocalDateTime.now());
            return transactionsRepository.save(depositTransaction);
        }
        else {
            // Handle the scenario where user is not found
            throw new UserNotFoundException("Username not present");
        }
    }

    public void updateTransactionStatus(String orderId, String username, double amount) {
        User user = userRepository.findByUsername(username);
        double currentBal = user.getAccountBalance();
        double updatedBalance;
        if (user.getUsername() != null) {
            if (currentBal == 0) {
                updatedBalance = amount;
            } else updatedBalance = user.getAccountBalance() + (amount);

            user.setAccountBalance(updatedBalance);
            userRepository.save(user);
            // Retrieve transaction from the database using transactionId
            Transaction transaction = transactionsRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + orderId));
            logger.info("Transaction Details:{}", transaction);
            logger.info("Txn amount: {} ", transaction.getAmount());
            logger.info("Txn status:{}", transaction.getTransStatus());

            //Later might be used
            //String userName=transaction.getUser().getUsername();

            // Update transaction status
            transaction.setTransStatus(TransStatus.UPDATED);
            // Save updated transaction to the database
            transactionsRepository.save(transaction);
            logger.info("Txn status updated:{}", transaction.getTransStatus());
            //logger.info(JSON.s(transactionsRepository.save(transaction)));
        }
    }

    @Value(value = "${apiKey}")
    private String apiKey;
    @Value(value = "${apiSecret}")
    private String apiSecret;

    public String getRazorpayPaymentPageUrl(Transaction transaction) {

        logger.info("Transaction DTO: {}", transaction );
        // Use Razorpay SDK to generate payment page URL
        try {
            // Initialize Razorpay client
            RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);
            // Create payment options
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", transaction.getAmount() * 100); // Amount in paise (100 paise = 1 INR)
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

            Order order = razorpayClient.orders.create(orderRequest);

            logger.info("order is:"+ order);
            String orderIdUpdate = order.get("id");
            String orderId = order.toString();
            logger.info("orderID is:"+ orderId);
            transaction.setOrderId(orderIdUpdate);
            transactionsRepository.save(transaction);
            logger.info("Updated txn dto:{}",transaction);
            // Create Razorpay order
            // Get payment page URL from the order
            logger.info("Razorpay request payload:" + orderId);
            // Return payment page URL
            return orderId;
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }
    }

        public void handlePaymentSuccess(String orderId, String paymentId) {
        Optional<Transaction> transactionOpt = transactionsRepository.findByOrderId(orderId);
        if (transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();
            transaction.setPayment_id(paymentId);
            transaction.setTransStatus(TransStatus.SUCCESS);
            transactionsRepository.save(transaction);
        }
    }

    public void handlePaymentFailure(String orderId) {
        Optional<Transaction> transactionOpt = transactionsRepository.findByOrderId(orderId);
        if (transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();
            transaction.setTransStatus(TransStatus.FAILED);
            transactionsRepository.save(transaction);
        }
    }

    @Value(value = "${razorpay.webhook.secret}")
    private String webhookSecret;
    public boolean verifyWebhookSignature(String payload, String razorpaySignature) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(webhookSecret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(payload.getBytes());
            String calculatedSignature = Base64.getEncoder().encodeToString(hash);
            return calculatedSignature.equals(razorpaySignature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public WithdrawDto withdrawByUsername(String username, double amount){
        logger.info("Request: {{},{}}", username,amount);
        User user=userRepository.findByUsername(username);
        Transaction withdrawTransaction = new Transaction();
        if(user != null) {
            logger.info("User validation passed. Inside try catch block -- withdrawByUsername method ");
            withdrawTransaction.setUser(user);
            withdrawTransaction.setTransactionType(TransactionType.WITHDRAW);
            withdrawTransaction.setAmount(amount);
            if (user.getAccountBalance() >= amount) {
                try {
                    logger.info("Account balance validation passed..");
                    //Create withdrawal transaction
                    withdrawTransaction.setTransStatus(TransStatus.SUBMITTED);
                    logger.info("Txn status updated to SUBMITTED");
                    userService.deductUserBalance(user.getUsername(), amount);
                    //withdrawTransaction.setTransactionTime();
                    transactionsRepository.save(withdrawTransaction);
                } catch (InternalError e) {
                    throw new InternalError(e.getMessage());
                }
            }else {
                withdrawTransaction.setTransStatus(TransStatus.FAILED);
                logger.info("Txn status updated to FAILED");
                throw new InsufficientBalanceException("Not enough balance");
            }
        }else {
            // Handle user not found scenario
            withdrawTransaction.setTransStatus(TransStatus.FAILED);
            throw new UserNotFoundException("User not found");
            //return ResponseEntity.badRequest().body("User not found");
        }
        transactionsRepository.save(withdrawTransaction);
        UserProfile userProfile=profileRepository.findByUsername(user.getUsername());
        logger.info("Response: {{},{},{},{}}",userProfile.getFullName(),userProfile.getBankAccountNumber(), userProfile.getIfsc(),amount );
        return new WithdrawDto(
                withdrawTransaction.getTransaction_id(),
                withdrawTransaction.getTransactionTime(),
                withdrawTransaction.getTransStatus(),
                userProfile.getFullName(),
                userProfile.getEmail(),
                userProfile.getPan(),
                userProfile.getBankAccountNumber(),
                userProfile.getIfsc(),
                withdrawTransaction.getAmount(),
                userProfile.getAadhaar()
        );

    }
    public void withdrawalConfirmation(Long trans_id, double amount){
        Transaction transaction = transactionsRepository.findById(trans_id).orElseThrow(() -> new RuntimeException("Transaction not found with id: " + trans_id));
        // Process withdrawal (deduct amount from user's balance)
        if (transaction!=null && transaction.getAmount()==amount) {
            //double withdrawalAmount = withdrawalRequest.getAmount();
            transaction.setTransStatus(TransStatus.SUCCESS);
            transactionsRepository.save(transaction);
            //return ResponseEntity.ok("Withdrawal request submitted successfully");
        } else {
            throw new EntityNotFoundException("Transaction not present");
        }
    }

    public List<FetchTransactionsDTO> getTxnByUsername(String username){
        List<Transaction> fetch= userRepository.findByUsername(username).getTransactions();
        return fetch.stream()
                .sorted(Comparator.comparing(Transaction::getTransactionTime).reversed())
                .map(transaction -> new FetchTransactionsDTO(
                        transaction.getTransaction_id(),
                        transaction.getAmount(),
                        transaction.getTransactionTime().toLocalDateTime(),
                        String.valueOf(transaction.getTransStatus()),
                        transaction.getTransactionType()
                ))
                .collect(Collectors.toList());
    }

    public List<WithdrawDto> getWithdrawalRequests(){
        List<Transaction> withdrawalTransactions = transactionsRepository.findByTransactionTypeOrderByTransactionTimeDesc(TransactionType.WITHDRAW);
        List<WithdrawDto> transactionDTOs = new ArrayList<>();

        for (Transaction transaction : withdrawalTransactions){
            UserProfile userProfile = profileRepository.findByUsername(transaction.getUser().getUsername());
            WithdrawDto dto = new WithdrawDto(
                    transaction.getTransaction_id(),
                    transaction.getTransactionTime(),
                    transaction.getTransStatus(),
                    userProfile.getFullName(),
                    userProfile.getEmail(),
                    userProfile.getPan(),
                    userProfile.getBankAccountNumber(),
                    userProfile.getIfsc(),
                    transaction.getAmount(),
                    userProfile.getAadhaar()

            );
            transactionDTOs.add(dto);
        }
        return transactionDTOs;
    }
}