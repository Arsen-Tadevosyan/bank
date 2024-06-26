package com.example.bankcommon.entity;

import com.example.bankcommon.entity.enums.Gender;
import com.example.bankcommon.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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