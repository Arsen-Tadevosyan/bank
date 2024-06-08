package com.example.bankmvc.impl;

import com.example.bankcommon.entity.Card;
import com.example.bankcommon.entity.Repay;
import com.example.bankcommon.entity.Transaction;
import com.example.bankcommon.entity.enums.StatusRepay;
import com.example.bankcommon.repositories.RepayRepository;
import com.example.bankcommon.service.CardService;
import com.example.bankcommon.service.impl.RepayServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepayServiceImplTest {

    @InjectMocks
    private RepayServiceImpl repayService;

    @Mock
    private RepayRepository repayRepository;

    @Mock
    private CardService cardService;

    private Transaction transaction;
    private Card card;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        transaction.setRemainingMoney(1000);
        transaction.setMonths(12);
        transaction.setIssueDate(LocalDate.now());

        card = new Card();
        card.setBalance(1500);
    }

    @Test
    void testSaveRepay() {
        when(repayRepository.save(any(Repay.class))).thenReturn(new Repay());
        repayService.save(transaction);
        verify(repayRepository, times(transaction.getMonths())).save(any(Repay.class));
    }

    @Test
    void testGetByTransaction() {
        Page<Repay> mockPage = Mockito.mock(Page.class);
        when(repayRepository.getByTransaction(transaction, Pageable.unpaged())).thenReturn(mockPage);
        Page<Repay> result = repayService.getByTransaction(transaction, Pageable.unpaged());
        Assertions.assertEquals(mockPage, result);
        verify(repayRepository, times(1)).getByTransaction(transaction, Pageable.unpaged());
    }

    @Test
    void testRepay() {
        int repayId = 1;
        Repay repay = new Repay();
        repay.setId(repayId);
        repay.setSize(100);
        repay.setStatus(StatusRepay.UNDONE);
        when(repayRepository.findById(repayId)).thenReturn(Optional.of(repay));
        when(cardService.save(card)).thenReturn(card);

        boolean result = repayService.repay(repayId, card);

        assertTrue(result);
        assertEquals(StatusRepay.DONE, repay.getStatus());
        assertEquals(LocalDate.now(), repay.getDueDate());
        verify(repayRepository, times(1)).findById(repayId);
        verify(repayRepository, times(1)).save(repay);
        verify(cardService, times(1)).save(card);
    }

    @Test
    void testGetById() {
        int repayId = 1;
        Repay repay = new Repay();
        when(repayRepository.findById(repayId)).thenReturn(Optional.of(repay));

        Optional<Repay> result = repayService.getById(repayId);

        assertTrue(result.isPresent());
        assertEquals(repay, result.get());
        verify(repayRepository, times(1)).findById(repayId);
    }

    @Test
    void testFindByPayDayBetween() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Repay> mockList = List.of(new Repay(), new Repay());
        when(repayRepository.findByPayDayBetween(yesterday, tomorrow)).thenReturn(mockList);

        List<Repay> result = repayService.findByPayDayBetween(yesterday, tomorrow);

        assertEquals(mockList, result);
        verify(repayRepository, times(1)).findByPayDayBetween(yesterday, tomorrow);
    }

    @Test
    void testFindCountByTransactionAndStatus() {
        Transaction transaction = new Transaction();
        StatusRepay statusRepay = StatusRepay.UNDONE;
        int expectedCount = 5;
        when(repayRepository.findCountByTransactionAndStatus(transaction, statusRepay)).thenReturn(expectedCount);

        int result = repayService.findCountByTransactionAndStatus(transaction, statusRepay);

        assertEquals(expectedCount, result);
        verify(repayRepository, times(1)).findCountByTransactionAndStatus(transaction, statusRepay);
    }

    @Test
    void testFindCountByTransaction() {
        Transaction transaction = new Transaction();
        int expectedCount = 10;
        when(repayRepository.findCountByTransaction(transaction)).thenReturn(expectedCount);

        int result = repayService.findCountByTransaction(transaction);

        assertEquals(expectedCount, result);
        verify(repayRepository, times(1)).findCountByTransaction(transaction);
    }
}
