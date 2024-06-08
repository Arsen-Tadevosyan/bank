package com.example.bankcommon.entity;

import com.example.bankcommon.entity.enums.MoneyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "card")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String number;

    private double balance;

    @Enumerated(EnumType.STRING)
    private MoneyType moneyType;

    @ManyToOne
    private User user;
}
