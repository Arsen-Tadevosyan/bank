package com.example.bank.repositories;

import com.example.bank.entity.Transfer;
import com.example.bank.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer,Integer> {

    Page<Transfer> findByFromOrTo(User from, User to, Pageable pageable);

}
