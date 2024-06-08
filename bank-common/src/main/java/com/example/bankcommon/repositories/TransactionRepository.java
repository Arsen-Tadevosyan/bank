package com.example.bankcommon.repositories;

import com.example.bankcommon.entity.Transaction;
import com.example.bankcommon.entity.User;
import com.example.bankcommon.entity.enums.Status;
import com.example.bankcommon.entity.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> , JpaSpecificationExecutor<Transaction> {

    Page<Transaction> findByUser(User user, Pageable pageable);

    List<Transaction> getByUser(User user);

    List<Transaction> findByUserAndStatus(User user, Status status);

    int countByUserAndTransactionTypeAndStatus(User user, TransactionType transactionType, Status status);

    List<Transaction> findByTransactionTypeAndStatus(TransactionType transactionType, Status status);

    List<Transaction> findByStatus( Status status);

    @Query("SELECT COUNT(t) FROM Transaction t")
    long count();
}
