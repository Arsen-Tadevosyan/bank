package com.example.bankmvc.impl;

import com.example.bankcommon.entity.Card;
import com.example.bankcommon.entity.Notification;
import com.example.bankcommon.entity.Transaction;
import com.example.bankcommon.entity.User;
import com.example.bankcommon.entity.enums.MoneyType;
import com.example.bankcommon.entity.enums.Status;
import com.example.bankcommon.entity.enums.TransactionType;
import com.example.bankcommon.repositories.TransactionRepository;
import com.example.bankcommon.service.CardService;
import com.example.bankcommon.service.NotificationService;
import com.example.bankcommon.service.impl.TransactionServiceImpl;
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
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardService cardService;

    @Mock
    private NotificationService notificationService;

    private User user;
    private Card card;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Doe");

        card = new Card();
        card.setBalance(1000);
        card.setMoneyType(MoneyType.USD);

        transaction = new Transaction();
        transaction.setUser(user);
        transaction.setSize(500);
        transaction.setPercentage(13);
        transaction.setStatus(Status.DURING);
        transaction.setTransactionType(TransactionType.PERSONAL);
    }

    @Test
    void testUpdateTransaction() {
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction updatedTransaction = transactionService.update(transaction);

        assertNotNull(updatedTransaction);
        assertEquals(transaction, updatedTransaction);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void testSaveValidPersonalTransaction() {
        when(transactionRepository.findByUserAndStatus(user, Status.DURING)).thenReturn(Collections.emptyList());
        when(cardService.gatByUser(user)).thenReturn(card);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction savedTransaction = transactionService.save(500, 24, user, TransactionType.PERSONAL);

        assertNotNull(savedTransaction);
        assertEquals(500, savedTransaction.getSize());
        assertEquals(13, savedTransaction.getPercentage());
        assertEquals(Status.DURING, savedTransaction.getStatus());
        assertEquals(TransactionType.PERSONAL, savedTransaction.getTransactionType());
    }

    @Test
    void testSaveValidEducationTransaction() {
        when(transactionRepository.findByUserAndStatus(user, Status.DURING)).thenReturn(Collections.emptyList());
        when(cardService.gatByUser(user)).thenReturn(card);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction savedTransaction = transactionService.save(500, 48, user, TransactionType.EDUCATION);

        assertNotNull(savedTransaction);
        assertEquals(500, savedTransaction.getSize());
        assertEquals(6, savedTransaction.getPercentage());
        assertEquals(Status.DURING, savedTransaction.getStatus());
        assertEquals(TransactionType.EDUCATION, savedTransaction.getTransactionType());
    }

    @Test
    void testSaveTransactionExceedsLimit() {
        List<Transaction> transactions = Collections.nCopies(8, new Transaction());
        when(transactionRepository.findByUserAndStatus(user, Status.DURING)).thenReturn(transactions);

        Transaction savedTransaction = transactionService.save(500, 24, user, TransactionType.PERSONAL);

        assertNull(savedTransaction);
        verify(cardService, never()).save(any(Card.class));
        verify(notificationService, never()).save(any(Notification.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testSaveTransactionInvalidMonths() {
        Transaction savedTransaction = transactionService.save(500, 10, user, TransactionType.PERSONAL);

        assertNull(savedTransaction);
        verify(cardService, never()).save(any(Card.class));
        verify(notificationService, never()).save(any(Notification.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testFindByUser() {
        Pageable pageable = Mockito.mock(Pageable.class);
        Page<Transaction> page = Mockito.mock(Page.class);
        when(transactionRepository.findByUser(user, pageable)).thenReturn(page);

        Page<Transaction> result = transactionService.findByUser(user, pageable);

        assertNotNull(result);
        verify(transactionRepository, times(1)).findByUser(user, pageable);
    }

    @Test
    void testGetByUser() {
        List<Transaction> transactions = List.of(transaction);
        when(transactionRepository.getByUser(user)).thenReturn(transactions);

        List<Transaction> result = transactionService.getByUser(user);

        assertNotNull(result);
        assertEquals(transactions, result);
        verify(transactionRepository, times(1)).getByUser(user);
    }

    @Test
    void testGetById() {
        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.getById(1);

        assertNotNull(result);
        assertEquals(transaction, result);
        verify(transactionRepository, times(1)).findById(1);
    }

    @Test
    void testFindByStatus() {
        List<Transaction> transactions = List.of(transaction);
        when(transactionRepository.findByStatus(Status.DURING)).thenReturn(transactions);

        List<Transaction> result = transactionService.findByStatus(Status.DURING);

        assertNotNull(result);
        assertEquals(transactions, result);
        verify(transactionRepository, times(1)).findByStatus(Status.DURING);
    }

    @Test
    void testSaveValidDeposit() {
        when(cardService.gatByUser(user)).thenReturn(card);
        when(transactionRepository.countByUserAndTransactionTypeAndStatus(user, TransactionType.DEPOSIT, Status.DURING)).thenReturn(2);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction savedTransaction = transactionService.saveDeposit(user, 500, 24);

        assertNotNull(savedTransaction);
        assertEquals(500, savedTransaction.getSize());
        assertEquals(2, savedTransaction.getPercentage());
        assertEquals(Status.DURING, savedTransaction.getStatus());
        assertEquals(TransactionType.DEPOSIT, savedTransaction.getTransactionType());
    }

    @Test
    void testSaveDepositExceedsLimit() {
        when(cardService.gatByUser(user)).thenReturn(card);
        when(transactionRepository.countByUserAndTransactionTypeAndStatus(user, TransactionType.DEPOSIT, Status.DURING)).thenReturn(3);

        Transaction savedTransaction = transactionService.saveDeposit(user, 500, 24);

        assertNull(savedTransaction);
        verify(cardService, never()).save(any(Card.class));
        verify(notificationService, never()).save(any(Notification.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testSaveFreeTimeDepositValid() {
        when(cardService.gatByUser(user)).thenReturn(card);
        when(transactionRepository.countByUserAndTransactionTypeAndStatus(user, TransactionType.DEPOSIT, Status.DURING)).thenReturn(2);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction savedTransaction = transactionService.saveFreeTimeDeposit(user, 500);

        assertNotNull(savedTransaction);
        assertEquals(500, savedTransaction.getSize());
        assertEquals(1, savedTransaction.getPercentage());
        assertEquals(Status.DURING, savedTransaction.getStatus());
        assertEquals(TransactionType.DEPOSIT, savedTransaction.getTransactionType());
    }

    @Test
    void testSaveFreeTimeDepositExceedsLimit() {
        when(cardService.gatByUser(user)).thenReturn(card);
        when(transactionRepository.countByUserAndTransactionTypeAndStatus(user, TransactionType.DEPOSIT, Status.DURING)).thenReturn(3);

        Transaction savedTransaction = transactionService.saveFreeTimeDeposit(user, 500);

        assertNull(savedTransaction);
        verify(cardService, never()).save(any(Card.class));
        verify(notificationService, never()).save(any(Notification.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testFindByTransactionTypeAndStatus() {
        List<Transaction> transactions = List.of(transaction);
        when(transactionRepository.findByTransactionTypeAndStatus(TransactionType.PERSONAL, Status.DURING)).thenReturn(transactions);

        List<Transaction> result = transactionService.findByTransactionTypeAndStatus(TransactionType.PERSONAL, Status.DURING);

        assertNotNull(result);
        assertEquals(transactions, result);
        verify(transactionRepository, times(1)).findByTransactionTypeAndStatus(TransactionType.PERSONAL, Status.DURING);
    }

    @Test
    void testFindBySpecification() {
        Specification<Transaction> specification = Mockito.mock(Specification.class);
        Pageable pageable = Mockito.mock(Pageable.class);
        Page<Transaction> page = Mockito.mock(Page.class);
        when(transactionRepository.findAll(specification, pageable)).thenReturn(page);

        Page<Transaction> result = transactionService.findBySpecification(specification, pageable);

        assertNotNull(result);
        verify(transactionRepository, times(1)).findAll(specification, pageable);
    }
}
