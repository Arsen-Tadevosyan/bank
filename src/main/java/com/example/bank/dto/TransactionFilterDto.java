package com.example.bank.dto;

import com.example.bank.entity.enums.MoneyType;
import com.example.bank.entity.enums.Status;
import com.example.bank.entity.enums.TransactionType;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransactionFilterDto {

    private MoneyType moneyType;

    private Status status;

    @Positive(message = "Minimum size must be a positive number")
    private Double minSize;

    @Positive(message = "Maximum size must be a positive number")
    private Double maxSize;

    private TransactionType transactionType;
}
