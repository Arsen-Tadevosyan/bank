package com.example.bankcommon.entity;

import com.example.bankcommon.entity.enums.MoneyType;
import com.example.bankcommon.entity.enums.Status;
import com.example.bankcommon.entity.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
