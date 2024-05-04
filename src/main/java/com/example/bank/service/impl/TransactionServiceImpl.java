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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CardService cardService;
    private final NotificationService notificationService;

    @Override
    public Transaction update(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public Transaction save(double size, int mounts, User user, TransactionType transactionType) {
        double percent = 0;
        List<Transaction> byUserAndStatus = transactionRepository.findByUserAndStatus(user, Status.DURING);
        if (byUserAndStatus.size() > 7) {
            return null;
        }
        switch (transactionType) {
            case PERSONAL:
                if (mounts < 12 || mounts > 60) {
                    return null;
                } else {
                    percent = 13;
                }
                break;
            case EDUCATION:
                if (mounts < 36 || mounts > 96) {
                    return null;
                } else {
                    percent = 6;
                }
                break;
            case BUSINESS:
                if (mounts < 36 || mounts > 120) {
                    return null;
                } else {
                    percent = 11;
                }
                break;
        }
        LocalDate finishDate = LocalDate.now();
        for (int i = 0; i < mounts; i++) {
            finishDate = LocalDate.now().plusMonths(i + 1);
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
                .remainingMoney(size + (size * percent / 100))
                .status(Status.DURING)
                .transactionType(transactionType)
                .user(user)
                .build());
    }

    @Override
    public Page<Transaction> findByUser(User user, Pageable pageable) {
        return transactionRepository.findByUser(user, pageable);
    }

    @Override
    public List<Transaction> getByUser(User user) {
        return transactionRepository.getByUser(user);
    }

    @Override
    public Transaction getById(int id) {
        return transactionRepository.findById(id).orElse(null);
    }

    @Override
    public List<Transaction> findByStatus(Status status) {
        return transactionRepository.findByStatus(status);
    }

    @Override
    public Page<Transaction> getAll(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Override
    public Page<Transaction> findBySpecification(Specification<Transaction> specification, Pageable pageable) {
        return transactionRepository.findAll(specification, pageable);
    }

//    @Override
//    public Page<Transaction> findByFilter(TransactionFilterDto transactionFilterDto, Pageable pageable) {
//        return transactionRepository.filterTransactions(transactionFilterDto,pageable);
//    }

//    @Override
//    public Page<Transaction> searchTransactions(TransactionFilterDto filter, Pageable pageable) {
//        return transactionRepository.findTransactionsByFilter(filter, pageable);
//    }

    @Override
    @Transactional
    public Transaction saveDeposit(User user, double size, int mounts) {
        if (mounts < 12 || mounts > 96) {
            return null;
        }
        Card card = cardService.gatByUser(user);
        if (card.getBalance() < size) {
            return null;
        }
        if (transactionRepository.countByUserAndTransactionTypeAndStatus(user, TransactionType.DEPOSIT, Status.DURING) >= 3) {
            return null;
        }
        cardService.save(card);
        notificationService.save(Notification.builder()
                .notificationType(NotificationType.INFO)
                .user(user)
                .dateDispatch(LocalDateTime.now())
                .message("   Your Deposit has been confirmed  +" + size + "|" + card.getMoneyType().name())
                .build());
        double balance = card.getBalance();
        card.setBalance(balance - size);
        cardService.save(card);
        return transactionRepository.save(Transaction.builder()
                .size(size)
                .moneyType(card.getMoneyType())
                .percentage(2)
                .issueDate(LocalDate.now())
                .finishDate(LocalDate.now().plusMonths(mounts))
                .remainingMoney(size)
                .status(Status.DURING)
                .user(user)
                .months(mounts)
                .transactionType(TransactionType.DEPOSIT)
                .build());

    }

    @Override
    public Transaction saveFreeTimeDeposit(User user, double size) {
        Card card = cardService.gatByUser(user);
        if (card.getBalance() < size) {
            return null;
        }
        if (transactionRepository.countByUserAndTransactionTypeAndStatus(user, TransactionType.DEPOSIT, Status.DURING) >= 3) {
            return null;
        }
        cardService.save(card);
        notificationService.save(Notification.builder()
                .notificationType(NotificationType.INFO)
                .user(user)
                .dateDispatch(LocalDateTime.now())
                .message("   Your Deposit has been confirmed  +" + size + "|" + card.getMoneyType().name())
                .build());
        double balance = card.getBalance();
        card.setBalance(balance - size);
        cardService.save(card);
        return transactionRepository.save(Transaction.builder()
                .size(size)
                .moneyType(card.getMoneyType())
                .percentage(1)
                .issueDate(LocalDate.now())
                .finishDate(LocalDate.now())
                .remainingMoney(size)
                .status(Status.DURING)
                .user(user)
                .months(0)
                .transactionType(TransactionType.DEPOSIT)
                .build());

    }

    @Override
    public List<Transaction> findByTransactionTypeAndStatus(TransactionType transactionType, Status status) {
        return transactionRepository.findByTransactionTypeAndStatus(transactionType, status);
    }
}
