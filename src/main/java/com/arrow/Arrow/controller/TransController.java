package com.arrow.Arrow.controller;

import com.arrow.Arrow.dto.CallBackDTO;
import com.arrow.Arrow.dto.FetchTransactionsDTO;
import com.arrow.Arrow.dto.TxnDto;
import com.arrow.Arrow.dto.WithdrawDto;
import com.arrow.Arrow.entity.Transaction;
import com.arrow.Arrow.services.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransController {

    Logger logger= LoggerFactory.getLogger(TransController.class);
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody TxnDto txnDto){
        logger.info("Request JSON : {}", txnDto);
        Transaction transaction =transactionService.depositByUsername(txnDto.getUsername(),txnDto.getAmount());
        String paymentPageUrl = transactionService.getRazorpayPaymentPageUrl(transaction.getTransaction_id(), transaction.getAmount());
        return ResponseEntity.ok(paymentPageUrl);
    }

    @PostMapping("/razorpay/callback")
    public ResponseEntity<?> handleRazorpayCallback(@RequestBody CallBackDTO callBack) {
        // Retrieve transaction ID from callback

        Long transactionId = callBack.getTransactionId();
        // Retrieve payment status from callback
        String paymentStatus = callBack.getPaymentStatus();
        String username= callBack.getUsername();
        double depositAmt= callBack.getAmount();
        // Update transaction status based on payment status
        transactionService.updateTransactionStatus(transactionId, paymentStatus, username, depositAmt);
        return ResponseEntity.ok("Callback received successfully");
    }

    @PostMapping("/withdrawPage")
    public  WithdrawDto withdraw(@RequestBody TxnDto txnDto){
        return transactionService.withdrawByUsername(txnDto.getUsername(),txnDto.getAmount());
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
