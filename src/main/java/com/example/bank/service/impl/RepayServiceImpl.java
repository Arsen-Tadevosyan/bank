package com.example.bank.service.impl;

import com.example.bank.entity.Repay;
import com.example.bank.entity.Transaction;
import com.example.bank.repositories.RepayRepository;
import com.example.bank.service.RepayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RepayServiceImpl implements RepayService {

    private final RepayRepository repayRepository;

    @Override
    public void save(Transaction transaction) {
        double repaySize = transaction.getRemainingMoney() / transaction.getMonths();
        for (int i = 0; i < transaction.getMonths(); i++) {
            LocalDate payDay = transaction.getIssueDate().plusMonths(1);
            repayRepository.save(Repay.builder()
                    .size(repaySize)
                    .payDay(payDay)
                    .transaction(transaction)
                    .dueDate(null)
                    .build());
        }
    }
}
