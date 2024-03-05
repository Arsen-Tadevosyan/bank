package com.example.bank.entity;

import com.example.bank.entity.enums.MoneyType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "card")
@Data
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String number;

    private double balance;

    @Enumerated(EnumType.STRING)
    private MoneyType moneyType;

    @ManyToOne
    private User user;
}
