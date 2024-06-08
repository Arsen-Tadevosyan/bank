package com.example.bankcommon.service.impl;

import com.example.bankcommon.entity.Transfer;
import com.example.bankcommon.entity.User;
import com.example.bankcommon.entity.enums.NotificationType;
import com.example.bankcommon.repositories.TransferRepository;
import com.example.bankcommon.service.TransferService;
import com.example.bankcommon.util.SendNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final SendNotification sendNotification;

    @Override
    public Transfer save(Transfer transfer) {
        String size = String.valueOf(transfer.getSize());
        sendNotification.sendNotification(transfer.getTo(), NotificationType.INFO,
                transfer.getFrom().getName() + " has transferred " + size +
                        "|" + transfer.getMoneyType().name() + " to your card");
        log.info("Transfer saved: {}", transfer);
        return transferRepository.save(transfer);
    }

    public Page<Transfer> getTransfersForUser(User user, Pageable pageable) {
        return transferRepository.findByFromOrTo(user, user, pageable);
    }
}
