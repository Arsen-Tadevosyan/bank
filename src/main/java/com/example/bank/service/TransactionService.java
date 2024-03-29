package com.example.bank.service;

import com.example.bank.entity.Transaction;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.TransactionType;

import java.util.List;

public interface TransactionService {

    Transaction update(Transaction transaction);

    Transaction save(double size, int mounts, User user, TransactionType transactionType);

    List<Transaction> findByUser(User user);

    Transaction getById(int id);

    List<Transaction> findAll();
}
