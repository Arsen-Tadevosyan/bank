package com.example.bankcommon.service;

import com.example.bankcommon.entity.AddWithdraw;
import com.example.bankcommon.entity.Card;
import com.example.bankcommon.entity.User;
import com.example.bankcommon.entity.enums.MoneyType;
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
