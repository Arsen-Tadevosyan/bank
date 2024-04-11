package com.example.bank.service;

import com.example.bank.entity.AddWithdraw;
import com.example.bank.entity.Card;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.MoneyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {

    Card save(Card card);

    boolean addMoney(double size, MoneyType moneyType, User user);

    Card gatByUser(User user);

    boolean withdrawMoney(double size, User user);

    Card findByNumber(String number);

    boolean transfer(double size, String cardNumber, User currentUser);

    AddWithdraw saveAddWithdraw(AddWithdraw addWithdraw);

    Page<AddWithdraw> gatByUser(User user, Pageable pageable);

}
