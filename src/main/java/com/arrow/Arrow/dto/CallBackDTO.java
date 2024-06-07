package com.arrow.Arrow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CallBackDTO {
    private Long transactionId;
    private String paymentStatus;
    private String username;
    private double amount;
}
