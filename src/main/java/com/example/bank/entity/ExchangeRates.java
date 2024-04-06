package com.example.bank.entity;

import com.example.bank.entity.enums.MoneyType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "exchange_rates")
@Data
public class ExchangeRates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private MoneyType sourceCurrency;

    @Enumerated(EnumType.STRING)
    private MoneyType targetCurrency;

    private double exchangeRate;
}
