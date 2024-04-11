package com.example.bank.repositories;

import com.example.bank.entity.AddWithdraw;
import com.example.bank.entity.Card;
import com.example.bank.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddWithdrawRepository extends JpaRepository<AddWithdraw, Integer> {

    AddWithdraw save(AddWithdraw addWithdraw);

    Page<AddWithdraw> findByUser(User user, Pageable pageable);

}
