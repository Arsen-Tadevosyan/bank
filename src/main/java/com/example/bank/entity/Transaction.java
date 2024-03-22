package com.example.bank.entity;

import com.example.bank.entity.enums.MoneyType;
import com.example.bank.entity.enums.Status;
import com.example.bank.entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "transaction")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double size;

    @Enumerated(EnumType.STRING)
    private MoneyType moneyType;

    private double percentage;

    private LocalDate issueDate;

    private int months;

    private LocalDate finishDate;

    private double remainingMoney;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @ManyToOne
    private User user;
}
