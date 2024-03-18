package com.example.bank.service.impl;

import com.example.bank.entity.Transfer;
import com.example.bank.entity.enums.NotificationType;
import com.example.bank.repositories.TransferRepository;
import com.example.bank.service.TransferService;
import com.example.bank.util.SendNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final SendNotification sendNotification;


    @Override
    public Transfer save(Transfer transfer) {
        String size = String.valueOf(transfer.getSize());
        sendNotification.sendNotification(transfer.getTo(), NotificationType.INFO
                , transfer.getFrom().getName() + " has transferred " + size +
                        "|"+ transfer.getMoneyType().name() + " to your card");
        return transferRepository.save(transfer);
    }
}
