package com.example.bank.service.impl;

import com.example.bank.entity.Card;
import com.example.bank.entity.Repay;
import com.example.bank.entity.Transaction;
import com.example.bank.entity.enums.StatusRepay;
import com.example.bank.repositories.RepayRepository;
import com.example.bank.service.CardService;
import com.example.bank.service.RepayService;
import lombok.RequiredArgsConstructor;
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

    @Override
    public void save(Transaction transaction) {
        double repaySize = transaction.getRemainingMoney() / transaction.getMonths();
        for (int i = 0; i < transaction.getMonths(); i++) {
            LocalDate payDay = transaction.getIssueDate().plusMonths(i + 1);
            repayRepository.save(Repay.builder()
                    .size(repaySize)
                    .payDay(payDay)
                    .transaction(transaction)
                    .status(StatusRepay.UNDONE)
                    .dueDate(null)
                    .build());
        }
    }

    @Override
    public Page<Repay> getByTransaction(Transaction transaction, Pageable pageable) {
        return repayRepository.findByTransaction(transaction, pageable);
    }

    @Override
    public boolean repay(int id, Card card) {
        Optional<Repay> byId = repayRepository.findById(id);
        Repay repay = byId.get();
        if (card.getBalance() < repay.getSize() || repay.getStatus() == StatusRepay.DONE) {
            return false;
        }
        repay.setStatus(StatusRepay.DONE);
        double balance = card.getBalance();
        card.setBalance(balance - repay.getSize());
        repay.setDueDate(LocalDate.now());
        repayRepository.save(repay);
        cardService.save(card);
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
    public Page<Repay> findByTransactionAndStatus(Transaction transaction, Pageable pageable, StatusRepay statusRepay) {
        return repayRepository.findByTransactionAndStatus(transaction, pageable, statusRepay);
    }
}
