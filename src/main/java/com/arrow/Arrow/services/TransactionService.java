package com.arrow.Arrow.services;

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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
        double currentBal=user.getAccountBalance();
        double updatedBalance;
        if(user.getUsername()!=null){
            if(currentBal == 0){
                updatedBalance=amount;
            }else {
                updatedBalance = user.getAccountBalance() + amount;
            }

            //user.setAccountBalance(updatedBalance);
            userRepository.save(user);
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

    public void updateTransactionStatus(Long transactionId, String status, String username, double amount) {
        User user = userRepository.findByUsername(username);
        double currentBal = user.getAccountBalance();
        double updatedBalance;
        if (user.getUsername() != null) {
            if (currentBal == 0) {
                updatedBalance = amount;
            } else updatedBalance = user.getAccountBalance() + (amount);

            //user.setAccountBalance(updatedBalance);
            // Retrieve transaction from the database using transactionId
            Transaction transaction = transactionsRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));

            // Update transaction status
            transaction.setTransStatus(TransStatus.valueOf(status));
            // Save updated transaction to the database
            transactionsRepository.save(transaction);
            //logger.info(JSON.s(transactionsRepository.save(transaction)));
        }
    }

    @Value(value = "${apiKey}")
    private String apiKey;
    @Value(value = "${apiSecret}")
    private String apiSecret;

    public String getRazorpayPaymentPageUrl(Long transactionId, double amount) {

        logger.info(String.valueOf(transactionId),amount);
        // Use Razorpay SDK to generate payment page URL
        try {
            // Initialize Razorpay client
            RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);
            // Create payment options
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount*100); // Amount in paise (100 paise = 1 INR)
            orderRequest.put("currency", "INR");
            //orderRequest.put("receipt", "order_rcptid_11");
            //orderRequest.put("upi",1);
            Order order = razorpayClient.orders.create(orderRequest);
            
            //QrCode qrCode=razorpayClient.qrCode.create(orderRequest);
            //return order.toString();

            // Create Razorpay order
            // Get payment page URL from the order
            String paymentPageUrl = order.toString();
            //String paymentPageUrl = String.valueOf(qrCode);
            logger.info("Razorpay request payload:" + paymentPageUrl);
            return paymentPageUrl;
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }

        // Return the URL
    }

    @Transactional
    public WithdrawDto withdrawByUsername(String username, double amount){
        logger.info("Request: {{},{}}", username,amount);
        User user=userRepository.findByUsername(username);
        //Create withdrawal transaction
        Transaction withdrawTransaction=new Transaction();
        withdrawTransaction.setUser(user);
        withdrawTransaction.setTransStatus(TransStatus.SUBMITTED);
        withdrawTransaction.setTransactionType(TransactionType.WITHDRAW);
        withdrawTransaction.setAmount(amount);
        //withdrawTransaction.setTransactionTime();
        Transaction savedTransaction=transactionsRepository.save(withdrawTransaction);


        // Process withdrawal (deduct amount from user's balance)
        if (user != null) {
            //double withdrawalAmount = withdrawalRequest.getAmount();
            if (userService.deductUserBalance(user.getUsername(), amount)) {
                // Update transaction status to SUCCESS if deduction was successful
                withdrawTransaction.setTransStatus(TransStatus.SUCCESS);
                transactionsRepository.save(withdrawTransaction);
                //return ResponseEntity.ok("Withdrawal request submitted successfully");
            } else {
                // If deduction failed, update transaction status to FAILED
                withdrawTransaction.setTransStatus(TransStatus.FAILED);
                transactionsRepository.save(withdrawTransaction);
                //return ResponseEntity.badRequest().body("Insufficient balance");
            }
        } else {
            // Handle user not found scenario
            withdrawTransaction.setTransStatus(TransStatus.FAILED);
            transactionsRepository.save(withdrawTransaction);
            //return ResponseEntity.badRequest().body("User not found");
        }
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
                withdrawTransaction.getAmount()
        );

    }


    public List<FetchTransactionsDTO> getTxnByUsername(String username){
        List<Transaction> fetch= userRepository.findByUsername(username).getTransactions();
        List<FetchTransactionsDTO> fetchTransactionsList=fetch.stream()
                .sorted(Comparator.comparing(Transaction::getTransactionTime).reversed())
                .map(transaction -> new FetchTransactionsDTO(
                        transaction.getTransaction_id(),
                        transaction.getAmount(),
                        transaction.getTransactionTime().toLocalDateTime(),
                        String.valueOf(transaction.getTransStatus()),
                        transaction.getTransactionType()
                ))
                .collect(Collectors.toList());

        return fetchTransactionsList;
    }

    public List<WithdrawDto> getWithdrawalRequests(){
        List<Transaction> withdrawalTransactions = transactionsRepository.findByTransactionType(TransactionType.WITHDRAW);
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
                    transaction.getAmount()
            );
            transactionDTOs.add(dto);
        }
        return transactionDTOs;
    }
}
