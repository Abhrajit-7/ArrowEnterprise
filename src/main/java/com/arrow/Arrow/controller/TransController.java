package com.arrow.Arrow.controller;

import com.arrow.Arrow.dto.ConfirmationRequest;
import com.arrow.Arrow.dto.FetchTransactionsDTO;
import com.arrow.Arrow.dto.TxnDto;
import com.arrow.Arrow.dto.WithdrawDto;
import com.arrow.Arrow.entity.Transaction;
import com.arrow.Arrow.repository.TransactionsRepository;
import com.arrow.Arrow.services.TransactionService;
import com.arrow.Arrow.util.TransStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.razorpay.Utils.verifySignature;

@RestController
@RequestMapping("/transaction")
public class TransController {

    Logger logger= LoggerFactory.getLogger(TransController.class);
    @Autowired
    private TransactionService transactionService;

    @Autowired
    TransactionsRepository transactionsRepository;

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody TxnDto txnDto) {
        logger.info("Request JSON : {}", txnDto);
        Transaction transaction = transactionService.depositByUsername(txnDto.getUsername(), txnDto.getAmount());
        String paymentPageUrl = transactionService.getRazorpayPaymentPageUrl(transaction);
        return ResponseEntity.ok(paymentPageUrl);
    }
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody String payload, @RequestHeader("X-Razorpay-Signature") String razorpaySignature) {
        logger.info("Inside webhook controller");
        try {
            logger.info("Inside try block");
            //String payloadJson = new ObjectMapper().writeValueAsString(payload);
            logger.info("Webhook payload: {}", payload);
            logger.info("Webhook key Validation successful");
            //String event = (String) payload.get("event");
            //Map<String, Object> payloadData = (Map<String, Object>) payload.get("payload");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode event = objectMapper.readTree(payload);

            String eventType = event.get("event").asText();
            if ("payment.captured".equals(eventType)) {
                logger.info("Inside validation statement for payment capture");
                //String orderId = (String) ((Map<String, Object>) payloadData.get("order")).get("id");
                String orderId = event.get("payload").get("payment").get("entity").get("order_id").asText();
                logger.info("Webhook orderID: {}", orderId);
                //String paymentId = (String) ((Map<String, Object>) payloadData.get("payment")).get("id");
                String paymentId = event.get("payload").get("payment").get("entity").get("id").asText();
                logger.info("Webhook payment ID: {}", paymentId);
                transactionService.handlePaymentSuccess(orderId, paymentId);
                //Optional<Transaction> transaction=transactionsRepository.findByOrderId(orderId);
                Transaction transaction = transactionsRepository.findByOrderId(orderId)
                        .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + orderId));
                String username = transaction.getUser().getUsername();
                double amount = transaction.getAmount();
                logger.info("Amount received is :{}", amount);
                if (transaction.getOrderId() != null && transaction.getTransStatus() == TransStatus.SUCCESS) {
                    try {
                        transactionService.updateTransactionStatus(orderId, username, amount);
                }catch(Exception ex){
                    throw new IllegalStateException("Already deposit is successful and balance is updated");
                }
            }


                } else if ("payment.failed".equals(eventType)) {
                    String orderId = event.get("payload").get("payment").get("entity").get("order_id").asText();
                    //String orderId = (String) ((Map<String, Object>) payloadData.get("order")).get("id");
                    transactionService.handlePaymentFailure(orderId);
                }
                return ResponseEntity.ok().build();
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/withdrawPage")
    public  WithdrawDto withdraw(@RequestBody TxnDto txnDto){
        return transactionService.withdrawByUsername(txnDto.getUsername(),txnDto.getAmount());
    }
    @PostMapping("/updateStatus")
    public ResponseEntity<?> confirmWithdraw(@RequestBody ConfirmationRequest confirmationRequest){
        transactionService.withdrawalConfirmation(confirmationRequest.getTrans_id(),confirmationRequest.getAmount());
        return ResponseEntity.ok().build();
    }
    @GetMapping("/withdrawals")
    public List<WithdrawDto> withdrawals(){
        return transactionService.getWithdrawalRequests();
    }
    @GetMapping("/{username}/fetchTransactions")
    public ResponseEntity<List<FetchTransactionsDTO>> getTxnByUsername(@PathVariable String username){
        List<FetchTransactionsDTO> transactions=transactionService.getTxnByUsername(username);
        return ResponseEntity.ok(transactions);
    }

}
