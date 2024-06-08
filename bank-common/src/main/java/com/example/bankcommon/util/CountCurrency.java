package com.example.bankcommon.util;

import com.example.bankcommon.entity.ExchangeRates;
import com.example.bankcommon.entity.enums.MoneyType;
import com.example.bankcommon.repositories.ExchangeRatesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CountCurrency {

    private final ExchangeRatesRepository exchangeRatesRepository;

    public double countCurrency(double size, MoneyType moneyType, MoneyType cardMoneyType) {
        if (cardMoneyType.equals(moneyType)) {
            return 1;
        }
        ExchangeRates exchangeRate = exchangeRatesRepository.findFirstBySourceCurrencyAndTargetCurrencyOrderByIdDesc(moneyType, cardMoneyType);
        return exchangeRate.getExchangeRate();
    }
}
