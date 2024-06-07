package com.arrow.Arrow.dto;

import com.arrow.Arrow.util.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FetchTransactionsDTO {

    private Long trans_id;
    private double amount;
    private LocalDateTime transaction_time;
    private String trans_status;
    private TransactionType transactionType;
}
