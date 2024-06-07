package com.arrow.Arrow.repository;

import com.arrow.Arrow.util.TransactionType;
import com.arrow.Arrow.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByTransactionType(TransactionType transactionType);
}
