package com.example.bankcommon.repositories;

import com.example.bankcommon.entity.Card;
import com.example.bankcommon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Integer> {

    Card findByUser(User user);

    Card findByNumber(String number);

}
