package com.example.bank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "repay")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Repay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double size;

    private LocalDate payDay;

    private Date dueDate;

    @ManyToOne
    private Transaction transaction;
}
