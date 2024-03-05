package com.example.bank.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "repay")
@Data
public class Repay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double size;

    private Date payDay;

    private Date dueDay;

    @ManyToOne
    private Transaction transaction;
}
