package com.example.bank.entity;

import com.example.bank.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "notification")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private String message;

    @ManyToOne
    private User user;

    private Date dateDispatch;
}
