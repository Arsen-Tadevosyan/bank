package com.example.bank.service.impl;

import com.example.bank.entity.AddWithdraw;
import com.example.bank.entity.Card;
import com.example.bank.entity.Transfer;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.MoneyType;
import com.example.bank.repositories.AddWithdrawRepository;
import com.example.bank.repositories.CardRepository;
import com.example.bank.service.CardService;
import com.example.bank.service.TransferService;
import com.example.bank.util.CountCurrency;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CountCurrency countCurrency;
    private final TransferService transferService;
    private final AddWithdrawRepository addWithdrawRepository;


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
    public boolean transfer(double size, String cardNumber, User currentUser) {
        Card toCard = findByNumber(cardNumber);
        if (toCard == null) {
            return false;
        }
        Card fromCard = gatByUser(currentUser);
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
        transferService.save(Transfer.builder()
                .size(size)
                .from(fromCard.getUser())
                .to(toCard.getUser())
                .moneyType(fromCard.getMoneyType())
                .dateDispatch(LocalDateTime.now())
                .build());
        return true;
    }

    @Override
    public AddWithdraw saveAddWithdraw(AddWithdraw addWithdraw) {
        return addWithdrawRepository.save(addWithdraw);
    }

    @Override
    public Page<AddWithdraw> gatByUser(User user, Pageable pageable) {
        return addWithdrawRepository.findByUser(user, pageable);
    }
}
