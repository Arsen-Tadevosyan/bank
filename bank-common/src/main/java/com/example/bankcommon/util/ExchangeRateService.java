package com.example.bankcommon.util;

import com.example.bankcommon.entity.ExchangeRateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ExchangeRateService {

    private static final String EXCHANGE_RATE_API_BASE_URL = "https://api.exchangerate-api.com/v4/latest/";

    private final RestTemplate restTemplate;

    public ExchangeRateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getExchangeRate(String baseCurrency, String targetCurrency) {
        try {
            String apiUrl = EXCHANGE_RATE_API_BASE_URL + baseCurrency.toUpperCase();
            ExchangeRateResponse response = restTemplate.getForObject(apiUrl, ExchangeRateResponse.class);
            if (response != null && response.getRates() != null) {
                Double exchangeRate = response.getRates().get(targetCurrency.toUpperCase());
                if (exchangeRate != null) {
                    return exchangeRate;
                } else {
                    log.error("Exchange rate for {} not found in the response.", targetCurrency);
                }
            } else {
                log.error("Invalid or empty response received from the exchange rate API.");
            }
        } catch (Exception e) {
            log.error("Error fetching exchange rate for {} to {}: {}", baseCurrency, targetCurrency, e.getMessage());
        }
        return 0.0;
    }
}
