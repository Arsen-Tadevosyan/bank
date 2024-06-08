package com.example.bankcommon.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreditRequestDto {

    @NotNull(message = "Size of money is required")
    @Positive(message = "Size of money must be a positive number")
    private Double sizeMoney;

    @NotNull(message = "Number of months is required")
    @Positive(message = "Number of months must be a positive number")
    private Integer months;
}
