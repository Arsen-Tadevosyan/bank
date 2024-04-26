package com.example.bank.repositories;

import com.example.bank.entity.Transaction;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.Status;
import com.example.bank.entity.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Page<Transaction> findByUser(User user, Pageable pageable);

    List<Transaction> findByUserAndStatus(User user, Status status);

    int countByUserAndTransactionTypeAndStatus(User user, TransactionType transactionType, Status status);

    List<Transaction> findByTransactionTypeAndStatus(TransactionType transactionType, Status status);
}
