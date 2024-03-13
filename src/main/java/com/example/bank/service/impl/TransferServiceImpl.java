package com.example.bank.service.impl;

import com.example.bank.entity.Transfer;
import com.example.bank.repositories.TransferRepository;
import com.example.bank.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;


    @Override
    public Transfer save(Transfer transfer) {
        return transferRepository.save(transfer);
    }
}
