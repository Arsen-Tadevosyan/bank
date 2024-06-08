package com.example.bankcommon.service;

import com.example.bankcommon.entity.Transfer;
import com.example.bankcommon.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransferService {

    Transfer save(Transfer transfer);


    Page<Transfer> getTransfersForUser(User user, Pageable pageable);
}
