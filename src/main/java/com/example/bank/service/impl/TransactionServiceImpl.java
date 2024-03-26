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
    public Transaction save(double size, int mounts, User user, TransactionType transactionType) {
        double percent = 0;
        switch (transactionType) {
            case PERSONAl:
                if (mounts < 12 || mounts > 60) {
                    percent = 13;
                    return null;
                }
                break;
            case EDUCATION:
                if (mounts < 36 || mounts > 96) {
                    percent = 6;
                    return null;
                }
                break;
            case BUSINESS:
                if (mounts < 36 || mounts > 120) {
                    percent = 11;
                    return null;
                }
                break;
        }
        LocalDate finishDate = null;
        for (int i = 0; i < mounts; i++) {
            finishDate = LocalDate.now().plusMonths(1);
        }
        Card card = cardService.gatByUser(user);
        log.info("{} {} transaction has been confirmed", user.getName(), transactionType.name());
        notificationService.save(Notification.builder()
                .notificationType(NotificationType.INFO)
                .user(user)
                .dateDispatch(LocalDateTime.now())
                .message("   Your " + transactionType.name() + " transaction has been confirmed  +" + size + "|" + card.getMoneyType().name())
                .build());
        double balance = card.getBalance();
        card.setBalance(balance + size);
        cardService.save(card);
        return transactionRepository.save(Transaction.builder()
                .size(size)
                .moneyType(card.getMoneyType())
                .percentage(percent)
                .issueDate(LocalDate.now())
                .months(mounts)
                .finishDate(finishDate)
                .remainingMoney(size / 100 * percent + size)
                .status(Status.DURING)
                .transactionType(TransactionType.PERSONAl)
                .user(user)
                .build());
    }
}
