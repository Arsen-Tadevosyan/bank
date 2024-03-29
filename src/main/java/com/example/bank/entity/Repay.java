package com.example.bank.entity;

import com.example.bank.entity.enums.StatusRepay;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    private LocalDate dueDate;

    @ManyToOne
    private Transaction transaction;

    @Enumerated(EnumType.STRING)
    private StatusRepay status;

}
