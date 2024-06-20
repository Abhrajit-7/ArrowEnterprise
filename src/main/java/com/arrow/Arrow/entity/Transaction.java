package com.arrow.Arrow.entity;

import com.arrow.Arrow.util.TransStatus;
import com.arrow.Arrow.util.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "transaction_details")
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transaction_id;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "payment_id")
    private String payment_id;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "trans_status", length = 30)
    private TransStatus transStatus;

    private double amount;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    @CreationTimestamp
    @Column(name = "transaction_time")
    private Timestamp transactionTime;
}
