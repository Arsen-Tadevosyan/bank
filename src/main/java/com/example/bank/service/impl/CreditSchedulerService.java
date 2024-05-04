package com.example.bank.service.impl;

import com.example.bank.entity.Repay;
import com.example.bank.entity.Transaction;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.NotificationType;
import com.example.bank.entity.enums.Status;
import com.example.bank.entity.enums.StatusRepay;
import com.example.bank.service.RepayService;
import com.example.bank.service.TransactionService;
import com.example.bank.service.UserService;
import com.example.bank.util.SendNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditSchedulerService {

    private final RepayService repayService;
    private final SendNotification sendNotification;
    private final UserService userService;
    private final TransactionService transactionService;


    @Scheduled(cron = "0 0 0 * * *")
    public void checkRepays() {

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);

        log.info("Checking repayments for today: {}", today);

        List<Repay> repays = repayService.findByPayDayBetween(yesterday, tomorrow);
        for (Repay repay : repays) {
            if (repay.getStatus() == StatusRepay.UNDONE) {

                if (repay.getPayDay().equals(yesterday)) {
                    User user = repay.getTransaction().getUser();
                    int rating = user.getRating();
                    if (rating != 0) {
                        user.setRating(rating - 1);
                        userService.save(user);
                    }
                    sendNotification.sendNotification(user, NotificationType.WARN,
                            "  you did not pay your credit on time, please pay with ID:" + repay.getId());
                    log.warn("Credit payment was not made on time for transaction ID {}.", repay.getId());
                }

                if (repay.getPayDay().equals(today)) {
                    sendNotification.sendNotification(repay.getTransaction().getUser(), NotificationType.WARN,
                            "  today is the day of payment of your credit, please pay with ID:" + repay.getId());
                }

                if (repay.getPayDay().equals(tomorrow)) {
                    sendNotification.sendNotification(repay.getTransaction().getUser(), NotificationType.INFO,
                            "  tomorrow is the day of payment of your credit, please pay with ID:" + repay.getId());
                }
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkTransactionsInProcess() {
        List<Transaction> transactions = transactionService.findByStatus(Status.DURING);

        for (Transaction transaction : transactions) {
            int countByTransactionAndStatus = repayService.findCountByTransactionAndStatus(transaction, StatusRepay.DONE);
            int countByTransaction = repayService.findCountByTransaction(transaction);

            if (countByTransaction == countByTransactionAndStatus && transaction.getStatus() != Status.FINISHED) {
                transaction.setStatus(Status.FINISHED);
                transactionService.update(transaction);
                sendNotification.sendNotification(transaction.getUser(), NotificationType.INFO,
                        "you have successfully repaid the credit, thank you for using our services");
                log.info("");
            }
        }
    }


}
