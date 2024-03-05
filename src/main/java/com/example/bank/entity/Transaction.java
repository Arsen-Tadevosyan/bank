package com.example.bank.entity;

import com.example.bank.entity.enums.MoneyType;
import com.example.bank.entity.enums.Status;
import com.example.bank.entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "transaction")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double size;

    @Enumerated(EnumType.STRING)
    private MoneyType moneyType;

    private double percentage;

    private Date issueDate;

    private Date finishDate;

    private double remainingMoney;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @ManyToOne
    private User user;
}
