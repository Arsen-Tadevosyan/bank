package com.example.bank.service;

import com.example.bank.entity.Transaction;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.TransactionType;

public interface TransactionService {

    Transaction save(double size, int mounts, User user, TransactionType transactionType);
}
