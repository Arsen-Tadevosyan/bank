package com.example.bank.service.impl;

import com.example.bank.entity.Card;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.MoneyType;
import com.example.bank.repositories.CardRepository;
import com.example.bank.security.SpringUser;
import com.example.bank.service.CardService;
import com.example.bank.util.CountCurrency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CountCurrency countCurrency;


    @Override
    public Card save(Card card) {
        return cardRepository.save(card);
    }

    @Override
    public boolean addMoney(double size, MoneyType moneyType, User user) {
        if (size < 0) {
            return false;
        }
        Card card = gatByUser(user);
        double balance = card.getBalance();
        MoneyType cardMoneyType = card.getMoneyType();
        double value = countCurrency.countCurrency(size, moneyType, cardMoneyType);
        card.setBalance(value + balance);
        cardRepository.save(card);
        return true;
    }

    @Override
    public Card gatByUser(User user) {
        return cardRepository.findByUser(user);
    }


    @Override
    public boolean withdrawMoney(double size, User user) {
        Card card = cardRepository.findByUser(user);
        double balance = card.getBalance();
        if (size > balance) {
            return false;
        }
        card.setBalance(balance - size);
        cardRepository.save(card);
        return true;
    }

    @Override
    public Card findByNumber(String number) {
        return cardRepository.findByNumber(number);
    }

    @Override
    public boolean transfer(double size, String cardNumber, SpringUser springUser) {
        Card toCard = findByNumber(cardNumber);
        if (toCard == null) {
            return false;
        }
        Card fromCard = gatByUser(springUser.getUser());
        double fromBalance = fromCard.getBalance();
        double toBalance = toCard.getBalance();
        if (fromBalance < size) {
            return false;
        }
        fromCard.setBalance(fromBalance - size);
        double value = countCurrency.countCurrency(size, fromCard.getMoneyType(), toCard.getMoneyType());
        toCard.setBalance(toBalance + value);
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
        return true;
    }

}
