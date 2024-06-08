package com.example.bankcommon.dto;

import com.example.bankcommon.entity.enums.MoneyType;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransferFilterDto {
    private String from;
    private String to;
    private MoneyType moneyType;

    @Positive(message = "Minimum size must be a positive number")
    private Double minSize;

    @Positive(message = "Maximum size must be a positive number")
    private Double maxSize;
}