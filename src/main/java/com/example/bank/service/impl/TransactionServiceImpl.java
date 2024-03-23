package com.example.bank.service.impl;

import com.example.bank.entity.Card;
import com.example.bank.entity.Notification;
import com.example.bank.entity.Transaction;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.NotificationType;
import com.example.bank.entity.enums.Status;
import com.example.bank.entity.enums.TransactionType;
import com.example.bank.repositories.TransactionRepository;
import com.example.bank.service.CardService;
import com.example.bank.service.NotificationService;
import com.example.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CardService cardService;
    private final NotificationService notificationService;

    @Override
    public Transaction saveForPersonal(double size, int mounts, User user) {
        if (mounts < 12 || mounts > 60) {
            return null;
        }
        LocalDate finishDate = null;
        for (int i = 0; i < mounts; i++) {
            finishDate = LocalDate.now().plusMonths(1);
        }
        Card card = cardService.gatByUser(user);
        log.info("{} personal transaction has been confirmed", user.getName());
        notificationService.save(Notification.builder()
                .notificationType(NotificationType.INFO)
                .user(user)
                .dateDispatch(LocalDateTime.now())
                .message("   Your personal transaction has been confirmed  +" + size + "|" + card.getMoneyType().name())
                .build());
        double balance = card.getBalance();
        card.setBalance(balance + size);
        cardService.save(card);
        return transactionRepository.save(Transaction.builder()
                .size(size)
                .moneyType(card.getMoneyType())
                .percentage(13)
                .issueDate(LocalDate.now())
                .months(mounts)
                .finishDate(finishDate)
                .remainingMoney(size / 100 * 13 + size)
                .status(Status.DURING)
                .transactionType(TransactionType.PERSONAl)
                .user(user)
                .build());
    }

    @Override
    public Transaction saveForEducation(double size, int mounts, User user) {
        if (mounts < 36 || mounts > 96) {
            return null;
        }
        LocalDate finishDate = null;
        for (int i = 0; i < mounts; i++) {
            finishDate = LocalDate.now().plusMonths(1);
        }
        Card card = cardService.gatByUser(user);
        log.info("{} education transaction has been confirmed", user.getName());
        notificationService.save(Notification.builder()
                .notificationType(NotificationType.INFO)
                .user(user)
                .dateDispatch(LocalDateTime.now())
                .message("   Your education transaction has been confirmed  +" + size + "|" + card.getMoneyType().name())
                .build());
        double balance = card.getBalance();
        card.setBalance(balance + size);
        cardService.save(card);
        return transactionRepository.save(Transaction.builder()
                .size(size)
                .moneyType(card.getMoneyType())
                .percentage(6)
                .issueDate(LocalDate.now())
                .months(mounts)
                .finishDate(finishDate)
                .remainingMoney(size / 100 * 6 + size)
                .status(Status.DURING)
                .transactionType(TransactionType.EDUCATION)
                .user(user)
                .build());
    }

    @Override
    public Transaction saveForBusiness(double size, int mounts, User user) {
        if (mounts < 36 || mounts > 120) {
            return null;
        }
        LocalDate finishDate = null;
        for (int i = 0; i < mounts; i++) {
            finishDate = LocalDate.now().plusMonths(1);
        }
        Card card = cardService.gatByUser(user);
        log.info("{} Business transaction has been confirmed", user.getName());
        notificationService.save(Notification.builder()
                .notificationType(NotificationType.INFO)
                .user(user)
                .dateDispatch(LocalDateTime.now())
                .message("   Your Business transaction has been confirmed  +" + size + "|" + card.getMoneyType().name())
                .build());
        double balance = card.getBalance();
        card.setBalance(balance + size);
        cardService.save(card);
        return transactionRepository.save(Transaction.builder()
                .size(size)
                .moneyType(card.getMoneyType())
                .percentage(10)
                .issueDate(LocalDate.now())
                .months(mounts)
                .finishDate(finishDate)
                .remainingMoney(size / 100 * 10 + size)
                .status(Status.DURING)
                .transactionType(TransactionType.BUSINESS)
                .user(user)
                .build());
    }
}
