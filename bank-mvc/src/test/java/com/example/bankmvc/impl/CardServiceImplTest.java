package com.example.bankmvc.impl;


import com.example.bankcommon.entity.AddWithdraw;
import com.example.bankcommon.entity.Card;
import com.example.bankcommon.entity.Transfer;
import com.example.bankcommon.entity.User;
import com.example.bankcommon.entity.enums.MoneyType;
import com.example.bankcommon.repositories.AddWithdrawRepository;
import com.example.bankcommon.repositories.CardRepository;
import com.example.bankcommon.service.TransferService;
import com.example.bankcommon.service.impl.CardServiceImpl;
import com.example.bankcommon.util.CountCurrency;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class CardServiceImplTest {

    @InjectMocks
    private CardServiceImpl cardServiceImpl;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CountCurrency countCurrency;
    @Mock
    private TransferService transferService;
    @Mock
    private AddWithdrawRepository addWithdrawRepository;

    private User user;
    private Card card;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Doe");

        card = new Card();
        card.setBalance(5000);
        card.setMoneyType(MoneyType.AMD);
        card.setUser(user);
    }

    @Test
    void testSave() {
        when(cardRepository.save(card)).thenReturn(card);

        Card savedCard = cardServiceImpl.save(card);

        assertNotNull(savedCard);
        assertEquals(card, savedCard);
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void testAddMoney_PositiveAmount() {
        when(cardRepository.findByUser(user)).thenReturn(card);
        when(countCurrency.countCurrency(1000.0, MoneyType.AMD, MoneyType.AMD)).thenReturn(1.0);

        boolean result = cardServiceImpl.addMoney(1000, MoneyType.AMD, user);

        assertTrue(result);
        assertEquals(6000.0, card.getBalance());
        verify(cardRepository).save(card);
        verify(countCurrency).countCurrency(1000.0, MoneyType.AMD, MoneyType.AMD);
    }

    @Test
    void testAddMoney_NegativeAmount() {
        boolean result = cardServiceImpl.addMoney(-1000, MoneyType.AMD, user);

        assertFalse(result);
        verify(cardRepository, never()).save(any(Card.class));
        verify(countCurrency, never()).countCurrency(anyDouble(), any(MoneyType.class), any(MoneyType.class));
    }

    @Test
    void testGatByUser() {
        when(cardRepository.findByUser(user)).thenReturn(card);

        Card resultCard = cardServiceImpl.gatByUser(user);

        assertNotNull(resultCard);
        assertEquals(card, resultCard);
        verify(cardRepository, times(1)).findByUser(user);
    }

    @Test
    void testWithdrawMoney_SufficientBalance() {
        when(cardRepository.findByUser(user)).thenReturn(card);

        boolean result = cardServiceImpl.withdrawMoney(1000, user);

        assertTrue(result);
        assertEquals(4000.0, card.getBalance());
        verify(cardRepository).save(card);
    }

    @Test
    void testWithdrawMoney_InsufficientBalance() {
        when(cardRepository.findByUser(user)).thenReturn(card);

        boolean result = cardServiceImpl.withdrawMoney(6000, user);

        assertFalse(result);
        assertEquals(5000.0, card.getBalance());
        verify(cardRepository, never()).save(card);
    }

    @Test
    void testFindByNumber() {
        String cardNumber = "1234567890";
        when(cardRepository.findByNumber(cardNumber)).thenReturn(card);

        Card resultCard = cardServiceImpl.findByNumber(cardNumber);

        assertNotNull(resultCard);
        assertEquals(card, resultCard);
        verify(cardRepository, times(1)).findByNumber(cardNumber);
    }

    @Test
    void testTransfer_SufficientBalance() {
        String cardNumber = "1234567890";
        Card toCard = new Card();
        toCard.setBalance(2000);
        toCard.setMoneyType(MoneyType.AMD);
        toCard.setUser(new User());

        when(cardRepository.findByNumber(cardNumber)).thenReturn(toCard);
        when(cardRepository.findByUser(user)).thenReturn(card);
        when(countCurrency.countCurrency(1000.0, MoneyType.AMD, MoneyType.AMD)).thenReturn(1.0);

        boolean result = cardServiceImpl.transfer(1000, cardNumber, user);

        assertTrue(result);
        assertEquals(4000.0, card.getBalance());
        assertEquals(2001.0, toCard.getBalance());
        verify(cardRepository).save(card);
        verify(cardRepository).save(toCard);
        verify(transferService).save(any(Transfer.class));
    }

    @Test
    void testTransfer_InsufficientBalance() {
        String cardNumber = "1234567890";
        Card toCard = new Card();
        toCard.setBalance(2000);
        toCard.setMoneyType(MoneyType.AMD);
        toCard.setUser(new User());

        when(cardRepository.findByNumber(cardNumber)).thenReturn(toCard);
        when(cardRepository.findByUser(user)).thenReturn(card);

        boolean result = cardServiceImpl.transfer(6000, cardNumber, user);

        assertFalse(result);
        assertEquals(5000.0, card.getBalance());
        assertEquals(2000.0, toCard.getBalance());
        verify(cardRepository, never()).save(card);
        verify(cardRepository, never()).save(toCard);
        verify(transferService, never()).save(any(Transfer.class));
    }

    @Test
    void testSaveAddWithdraw() {
        AddWithdraw addWithdraw = new AddWithdraw();
        when(addWithdrawRepository.save(addWithdraw)).thenReturn(addWithdraw);

        AddWithdraw savedAddWithdraw = cardServiceImpl.saveAddWithdraw(addWithdraw);

        assertNotNull(savedAddWithdraw);
        assertEquals(addWithdraw, savedAddWithdraw);
        verify(addWithdrawRepository, times(1)).save(addWithdraw);
    }

    @Test
    void testGatByUserPageable() {
        Pageable pageable = Mockito.mock(Pageable.class);
        Page<AddWithdraw> page = Mockito.mock(Page.class);
        when(addWithdrawRepository.findByUser(user, pageable)).thenReturn(page);

        Page<AddWithdraw> resultPage = cardServiceImpl.gatByUser(user, pageable);

        assertNotNull(resultPage);
        verify(addWithdrawRepository, times(1)).findByUser(user, pageable);
    }
}
