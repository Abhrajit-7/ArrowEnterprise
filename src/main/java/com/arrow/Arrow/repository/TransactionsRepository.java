package com.arrow.Arrow.repository;

import com.arrow.Arrow.util.TransactionType;
import com.arrow.Arrow.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByTransactionTypeOrderByTransactionTimeDesc(TransactionType transactionType);
    Optional<Transaction> findByOrderId(String order_id);
}
