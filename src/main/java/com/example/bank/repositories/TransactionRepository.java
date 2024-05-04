package com.example.bank.repositories;

import com.example.bank.entity.Transaction;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> , JpaSpecificationExecutor<Transaction> {

    Page<Transaction> findByUser(User user, Pageable pageable);

    List<Transaction> getByUser(User user);

    List<Transaction> findByUserAndStatus(User user, Status status);

    List<Transaction> findByStatus( Status status);
}
