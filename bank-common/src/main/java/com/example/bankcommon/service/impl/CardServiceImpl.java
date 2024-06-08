package com.example.bankcommon.service.impl;

import com.example.bankcommon.entity.AddWithdraw;
import com.example.bankcommon.entity.Card;
import com.example.bankcommon.entity.Transfer;
import com.example.bankcommon.entity.User;
import com.example.bankcommon.entity.enums.MoneyType;
import com.example.bankcommon.repositories.AddWithdrawRepository;
import com.example.bankcommon.repositories.CardRepository;
import com.example.bankcommon.service.CardService;
import com.example.bankcommon.service.TransferService;
import com.example.bankcommon.util.CountCurrency;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CountCurrency countCurrency;
    private final TransferService transferService;
    private final AddWithdrawRepository addWithdrawRepository;

    private static final Logger logger = LoggerFactory.getLogger(CardServiceImpl.class);

    @Override
    public Card save(Card card) {
        logger.info("Saving card: {}", card);
        return cardRepository.save(card);
    }

    @Override
    @Transactional
    public boolean addMoney(double size, MoneyType moneyType, User user) {
        logger.info("Adding money: size={}, moneyType={}, user={}", size, moneyType, user);
        if (size < 0) {
            logger.warn("Cannot add negative amount: {}", size);
            return false;
        }
        Card card = gatByUser(user);
        double balance = card.getBalance();
        MoneyType cardMoneyType = card.getMoneyType();
        double currency = countCurrency.countCurrency(size, moneyType, cardMoneyType);
        double value = currency * size;
        card.setBalance(value + balance);
        cardRepository.save(card);
        logger.info("Money added successfully: new balance={}", card.getBalance());
        return true;
    }

    @Override
    public Card gatByUser(User user) {
        logger.info("Fetching card by user: {}", user);
        return cardRepository.findByUser(user);
    }

    @Override
    @Transactional
    public boolean withdrawMoney(double size, User user) {
        logger.info("Withdrawing money: size={}, user={}", size, user);
        Card card = cardRepository.findByUser(user);
        double balance = card.getBalance();
        if (size > balance) {
            logger.warn("Insufficient balance: balance={}, withdrawal size={}", balance, size);
            return false;
        }
        card.setBalance(balance - size);
        cardRepository.save(card);
        logger.info("Money withdrawn successfully: new balance={}", card.getBalance());
        return true;
    }

    @Override
    public Card findByNumber(String number) {
        logger.info("Fetching card by number: {}", number);
        return cardRepository.findByNumber(number);
    }

    @Override
    @Transactional
    public boolean transfer(double size, String cardNumber, User currentUser) {
        logger.info("Transferring money: size={}, fromUser={}, toCardNumber={}", size, currentUser, cardNumber);
        Card toCard = findByNumber(cardNumber);
        if (toCard == null) {
            logger.warn("Destination card not found: cardNumber={}", cardNumber);
            return false;
        }
        Card fromCard = gatByUser(currentUser);
        double fromBalance = fromCard.getBalance();
        if (fromBalance < size) {
            logger.warn("Insufficient balance for transfer: fromBalance={}, transfer size={}", fromBalance, size);
            return false;
        }
        fromCard.setBalance(fromBalance - size);
        double value = countCurrency.countCurrency(size, fromCard.getMoneyType(), toCard.getMoneyType());
        toCard.setBalance(toCard.getBalance() + value);
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
        transferService.save(Transfer.builder()
                .size(size)
                .from(fromCard.getUser())
                .to(toCard.getUser())
                .moneyType(fromCard.getMoneyType())
                .dateDispatch(LocalDateTime.now())
                .build());
        logger.info("Transfer successful: fromUser={}, toUser={}, transfer size={}", currentUser, toCard.getUser(), size);
        return true;
    }

    @Override
    public AddWithdraw saveAddWithdraw(AddWithdraw addWithdraw) {
        logger.info("Saving AddWithdraw: {}", addWithdraw);
        return addWithdrawRepository.save(addWithdraw);
    }

    @Override
    public Page<AddWithdraw> gatByUser(User user, Pageable pageable) {
        logger.info("Fetching AddWithdraw by user: {}", user);
        return addWithdrawRepository.findByUser(user, pageable);
    }
}
