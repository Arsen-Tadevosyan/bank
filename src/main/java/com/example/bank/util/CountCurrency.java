package com.example.bank.util;

import com.example.bank.entity.ExchangeRates;
import com.example.bank.entity.enums.MoneyType;
import com.example.bank.repositories.ExchangeRatesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CountCurrency {

    private final ExchangeRatesRepository exchangeRatesRepository;

    public double countCurrency(double size, MoneyType moneyType, MoneyType cardMoneyType) {
        if (cardMoneyType.equals(moneyType)) {
            return size;
        }
        ExchangeRates exchangeRate = exchangeRatesRepository.findFirstBySourceCurrencyAndTargetCurrencyOrderByIdDesc(moneyType, cardMoneyType);
        return exchangeRate.getExchangeRate();
    }
}
