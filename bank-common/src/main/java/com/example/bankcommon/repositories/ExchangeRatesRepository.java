package com.example.bankcommon.repositories;

import com.example.bankcommon.entity.ExchangeRates;
import com.example.bankcommon.entity.enums.MoneyType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRatesRepository extends JpaRepository<ExchangeRates, Integer> {

    ExchangeRates findFirstBySourceCurrencyAndTargetCurrencyOrderByIdDesc(MoneyType sourceCurrency, MoneyType targetCurrency);

}
