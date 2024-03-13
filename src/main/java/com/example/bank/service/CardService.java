package com.example.bank.service;

import com.example.bank.entity.Card;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.MoneyType;

public interface CardService {

    Card save(Card card);

    boolean addMoney(double size, MoneyType moneyType, User user);

    Card gatByUser(User user);

    boolean withdrawMoney(double size, User user);

    Card findByNumber(String number);

    boolean transfer(double size, String cardNumber, User currentUser);
}
