package com.arrow.Arrow.controller;

import com.arrow.Arrow.dto.DepositRequest;
import com.arrow.Arrow.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/initiate")
    public Map<String, String> initiatePayment(@RequestBody DepositRequest requestDTO) throws NoSuchAlgorithmException {
        return paymentService.initiatePayment(requestDTO);
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam Map<String, String> params) {
        // Handle success logic here
        return "Payment Successful!";
    }

    @GetMapping("/failure")
    public String paymentFailure(@RequestParam Map<String, String> params) {
        // Handle failure logic here
        return "Payment Failed!";
    }
}
