package com.example.bank.service.impl;

import com.example.bank.entity.Card;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.MoneyType;
import com.example.bank.repositories.CardRepository;
import com.example.bank.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;


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
        double value = countCurrency(size, moneyType, cardMoneyType);
        card.setBalance(value + balance);
        cardRepository.save(card);
        return true;
    }

    @Override
    public Card gatByUser(User user) {
        return cardRepository.findByUser(user);
    }

    @Override
    public double countCurrency(double size, MoneyType moneyType, MoneyType cardMoneyType) {
        double value = 0;
        if (cardMoneyType.equals(moneyType)) {
            return size;
        } else {
            switch (moneyType) {
                case AMD:
                    switch (cardMoneyType) {
                        case RUB:
                            value = size / 5;
                            break;
                        case USD:
                            value = size / 400;
                            break;
                    }
                    break;
                case USD:
                    switch (cardMoneyType) {
                        case RUB:
                            value = size * 90;
                            break;
                        case AMD:
                            value = size * 400;
                            break;
                    }
                    break;
                case RUB:
                    switch (cardMoneyType) {
                        case USD:
                            value = size / 0.011;
                            break;
                        case AMD:
                            value = size * 5;
                            break;
                    }
                    break;
            }
        }
        return value;
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

}
