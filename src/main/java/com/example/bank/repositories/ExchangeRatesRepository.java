package com.example.bank.repositories;

import com.example.bank.entity.ExchangeRates;
import com.example.bank.entity.enums.MoneyType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRatesRepository extends JpaRepository<ExchangeRates, Integer> {

    ExchangeRates findFirstBySourceCurrencyAndTargetCurrencyOrderByIdDesc(MoneyType sourceCurrency, MoneyType targetCurrency);

}
