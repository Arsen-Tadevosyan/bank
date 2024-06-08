package com.example.bankcommon.entity;

import com.example.bankcommon.entity.enums.MoneyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "transfer")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double size;

    @ManyToOne
    private User from;

    @ManyToOne
    private User to;

    @Enumerated(EnumType.STRING)
    private MoneyType moneyType;

    private LocalDateTime dateDispatch;
}
