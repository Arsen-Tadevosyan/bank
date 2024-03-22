package com.example.bank.service;

import com.example.bank.entity.Transaction;
import com.example.bank.entity.User;

public interface TransactionService {

    Transaction saveForPersonal(double size, int mounts, User user);

    Transaction saveForEducation(double size, int mounts, User user);

    Transaction saveForBusiness(double size, int mounts, User user);
}
