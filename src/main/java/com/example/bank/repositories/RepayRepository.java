package com.example.bank.repositories;

import com.example.bank.entity.Repay;
import com.example.bank.entity.Transaction;
import com.example.bank.entity.enums.StatusRepay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RepayRepository extends JpaRepository<Repay, Integer> {

    List<Repay> findByTransaction(Transaction transaction);

    Page<Repay> getByTransaction(Transaction transaction, Pageable pageable);

    List<Repay> findByTransactionAndStatus(Transaction transaction, StatusRepay status);

    Page<Repay> findByTransactionAndStatus(Transaction transaction, Pageable pageable, StatusRepay statusRepay);

    List<Repay> findByPayDayBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    int findCountByTransactionAndStatus(Transaction transaction, StatusRepay statusRepay);

    int findCountByTransaction(Transaction transaction);


}
