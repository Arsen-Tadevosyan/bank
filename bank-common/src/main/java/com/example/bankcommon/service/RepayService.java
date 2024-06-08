package com.example.bankcommon.service;

import com.example.bankcommon.entity.Card;
import com.example.bankcommon.entity.Repay;
import com.example.bankcommon.entity.Transaction;
import com.example.bankcommon.entity.enums.StatusRepay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RepayService {

    void save(Transaction transaction);

    Page<Repay> getByTransaction(Transaction transaction, Pageable pageable);

    boolean repay(int id, Card card);

    Optional<Repay> getById(int id);

    List<Repay> findByPayDayBetween(LocalDate yesterday, LocalDate tomorrow);

    int findCountByTransactionAndStatus(Transaction transaction, StatusRepay statusRepay);

    int findCountByTransaction(Transaction transaction);

    List<Repay> findByTransaction(Transaction transaction);


    Page<Repay> findByTransactionAndStatus(Transaction transaction, Pageable pageable, StatusRepay statusRepay);

}
