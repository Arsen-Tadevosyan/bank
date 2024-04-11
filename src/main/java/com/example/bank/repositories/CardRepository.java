package com.example.bank.repositories;

import com.example.bank.entity.Card;
import com.example.bank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Integer> {

    Card findByUser(User user);

    Card findByNumber(String number);

}
