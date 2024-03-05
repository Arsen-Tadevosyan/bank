package com.example.bank.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "message")
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private User from;

    @ManyToOne
    private User to;

    private String message;
}
