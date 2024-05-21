package com.example.bank.entity;

import com.example.bank.entity.enums.Gender;
import com.example.bank.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String surname;

    private String email;

    private String password;

    private int rating;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private int token;

    private boolean active;

    private int age;

    private String phone;

    private String picName;

    @Enumerated(EnumType.STRING)
    private Gender gender;
}