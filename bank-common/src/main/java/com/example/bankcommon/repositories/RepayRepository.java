package com.example.bankcommon.repositories;

import com.example.bankcommon.entity.Repay;
import com.example.bankcommon.entity.Transaction;
import com.example.bankcommon.entity.enums.StatusRepay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RepayRepository extends JpaRepository<Repay, Integer> {

    List<Repay> findByTransaction(Transaction transaction);

    Page<Repay> getByTransaction(Transaction transaction, Pageable pageable);

    Page<Repay> findByTransactionAndStatus(Transaction transaction, Pageable pageable, StatusRepay statusRepay);

    List<Repay> findByPayDayBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    int findCountByTransactionAndStatus(Transaction transaction, StatusRepay statusRepay);

    int findCountByTransaction(Transaction transaction);


}
