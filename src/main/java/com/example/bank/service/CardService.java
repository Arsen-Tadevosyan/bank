package com.example.bank.service;

import com.example.bank.entity.Card;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.MoneyType;

public interface CardService {

    Card save(Card card);

    boolean addMoney(double size, MoneyType moneyType, User user);

    Card gatByUser(User user);

    double countCurrency(double size,MoneyType moneyType,MoneyType cardMoneyType);

    boolean withdrawMoney(double size,User user);
}
