package com.example.bank.entity;

import com.example.bank.entity.enums.MoneyType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "transfer")
@Data
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

    private Date dateDispatch;
}
