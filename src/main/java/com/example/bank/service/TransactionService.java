package com.example.bank.service;

import com.example.bank.entity.Transaction;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.Status;
import com.example.bank.entity.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface TransactionService {

    Transaction update(Transaction transaction);

    Transaction save(double size, int mounts, User user, TransactionType transactionType);

    Page<Transaction> findByUser(User user, Pageable pageable);

    List<Transaction> getByUser(User user);

    Transaction getById(int id);


    Transaction saveDeposit(User user, double size, int mounts);

    Transaction saveFreeTimeDeposit(User user, double size);

    List<Transaction> findByTransactionTypeAndStatus(TransactionType transactionType, Status status);
    List<Transaction> findByStatus(Status status);

    Page<Transaction> findBySpecification(Specification<Transaction> spec, Pageable pageable);

}
