package com.example.bankcommon.repositories;

import com.example.bankcommon.entity.AddWithdraw;
import com.example.bankcommon.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddWithdrawRepository extends JpaRepository<AddWithdraw, Integer> {

    AddWithdraw save(AddWithdraw addWithdraw);

    Page<AddWithdraw> findByUser(User user, Pageable pageable);

}
