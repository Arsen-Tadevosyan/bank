package com.example.bank.entity;

import com.example.bank.entity.enums.MoneyType;
import com.example.bank.entity.enums.StatusAddWithdraw;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "add_withdraw")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddWithdraw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double size;

    @Enumerated(EnumType.STRING)
    private MoneyType moneyType;

    @Enumerated(EnumType.STRING)
    private StatusAddWithdraw status;

    private LocalDateTime dateTime;

    @ManyToOne
    private User user;

}
