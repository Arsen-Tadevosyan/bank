package com.example.bankcommon.service.impl;

import com.example.bankcommon.entity.Card;
import com.example.bankcommon.entity.Repay;
import com.example.bankcommon.entity.Transaction;
import com.example.bankcommon.entity.enums.StatusRepay;
import com.example.bankcommon.repositories.RepayRepository;
import com.example.bankcommon.service.CardService;
import com.example.bankcommon.service.RepayService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepayServiceImpl implements RepayService {

    private final RepayRepository repayRepository;
    private final CardService cardService;

    private static final Logger logger = LoggerFactory.getLogger(RepayServiceImpl.class);

    @Override
    public void save(Transaction transaction) {
        double repaySize = transaction.getRemainingMoney() / transaction.getMonths();
        for (int i = 0; i < transaction.getMonths(); i++) {
            LocalDate payDay = transaction.getIssueDate().plusMonths(i + 1);
            Repay newRepay = Repay.builder()
                    .size(repaySize)
                    .payDay(payDay)
                    .transaction(transaction)
                    .status(StatusRepay.UNDONE)
                    .dueDate(null)
                    .build();
            repayRepository.save(newRepay);
            logger.info("Repay saved: {}", newRepay);
        }
    }

    @Override
    public Page<Repay> getByTransaction(Transaction transaction, Pageable pageable) {
        Page<Repay> repays = repayRepository.getByTransaction(transaction, pageable);
        logger.info("Found {} repays for transaction: {}", repays.getTotalElements(), transaction.getId());
        return repays;
    }

    @Override
    public boolean repay(int id, Card card) {
        Optional<Repay> byId = repayRepository.findById(id);
        Repay repay = byId.get();
        if (card.getBalance() < repay.getSize() || repay.getStatus() == StatusRepay.DONE) {
            logger.warn("Repay with ID {} cannot be repaid. Insufficient balance or already repaid.", id);
            return false;
        }
        repay.setStatus(StatusRepay.DONE);
        double balance = card.getBalance();
        card.setBalance(balance - repay.getSize());
        repay.setDueDate(LocalDate.now());
        repayRepository.save(repay);
        cardService.save(card);
        logger.info("Repay with ID {} repaid successfully.", id);
        return true;
    }
    @Override
    public Optional<Repay> getById(int id) {
        return repayRepository.findById(id);
    }

    @Override
    public List<Repay> findByPayDayBetween(LocalDate yesterday, LocalDate tomorrow) {
        return repayRepository.findByPayDayBetween(yesterday, tomorrow);
    }

    @Override
    public int findCountByTransactionAndStatus(Transaction transaction, StatusRepay statusRepay) {
        return repayRepository.findCountByTransactionAndStatus(transaction, statusRepay);
    }

    @Override
    public int findCountByTransaction(Transaction transaction) {
        return repayRepository.findCountByTransaction(transaction);
    }

    @Override
    public List<Repay> findByTransaction(Transaction transaction) {
        return repayRepository.findByTransaction(transaction);
    }

    @Override
    public Page<Repay> findByTransactionAndStatus(Transaction transaction, Pageable pageable, StatusRepay statusRepay) {
        return repayRepository.findByTransactionAndStatus(transaction, pageable, statusRepay);
    }
}
