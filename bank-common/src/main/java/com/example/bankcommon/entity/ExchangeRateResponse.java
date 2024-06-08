package com.example.bankcommon.entity;

import lombok.Data;

import java.util.Map;

@Data
public class ExchangeRateResponse {
    private String base;
    private Map<String, Double> rates;
}
