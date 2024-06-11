package com.arrow.Arrow.services;

import com.arrow.Arrow.component.HashUtil;
import com.arrow.Arrow.dto.DepositRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    @Value("${payu.merchant.key}")
    private String merchantKey;

    @Value("${payu.merchant.salt}")
    private String merchantSalt;

    @Value("${payu.base.url}")
    private String baseUrl;

    @Value("${payu.success.url}")
    private String successUrl;

    @Value("${payu.failure.url}")
    private String failureUrl;

    public Map<String, String> initiatePayment(DepositRequest requestDTO) throws NoSuchAlgorithmException {
        String txnid = UUID.randomUUID().toString().replace("-", "");

        Map<String, String> params = new HashMap<>();
        params.put("key", merchantKey);
        params.put("txnid", txnid);
        params.put("amount", requestDTO.getAmount());
        params.put("productinfo", requestDTO.getProductInfo());
        params.put("firstname", requestDTO.getFirstname());
        params.put("email", requestDTO.getEmail());
        params.put("phone", requestDTO.getPhone());
        params.put("surl", successUrl);
        params.put("furl", failureUrl);

        String hashString = merchantKey + "|"  + txnid + "|" + requestDTO.getAmount() + "|" + requestDTO.getProductInfo() + "|" +
                requestDTO.getFirstname() + "|" + requestDTO.getEmail() + "|||||||||||" + merchantSalt;

        String hash = HashUtil.generateHash(hashString);
        params.put("hash", hash);
        params.put("action", baseUrl);

        return params;
    }
}

