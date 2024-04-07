package com.example.bank.service;

import com.example.bank.entity.Transfer;
import com.example.bank.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransferService {

    Transfer save(Transfer transfer);


    Page<Transfer> getTransfersForUser(User user, Pageable pageable);
}
