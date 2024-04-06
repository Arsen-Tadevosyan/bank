package com.example.bank.service.impl;

import com.example.bank.entity.ExchangeRates;
import com.example.bank.entity.enums.MoneyType;
import com.example.bank.repositories.ExchangeRatesRepository;
import com.example.bank.util.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateSchedulerService {

    private final ExchangeRateService exchangeRateService;
    private final ExchangeRatesRepository exchangeRatesRepository;

    @Scheduled(fixedDelay = 12 * 60 * 60 * 1000)
    public void getAndSaveAllExchangeRates() {
        MoneyType[] moneyTypes = MoneyType.values();

        for (MoneyType baseCurrency : moneyTypes) {
            for (MoneyType targetCurrency : moneyTypes) {
                if (baseCurrency != targetCurrency) {
                    double exchangeRate = exchangeRateService.getExchangeRate(baseCurrency.name(), targetCurrency.name());
                    ExchangeRates exchangeRateEntity = new ExchangeRates();
                    exchangeRateEntity.setSourceCurrency(baseCurrency);
                    exchangeRateEntity.setTargetCurrency(targetCurrency);
                    exchangeRateEntity.setExchangeRate(exchangeRate);
                    exchangeRatesRepository.save(exchangeRateEntity);
                }
            }
        }
        log.info("All exchange rates saved in the database.");
    }

}
