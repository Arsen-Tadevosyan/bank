package com.example.bank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserFilterDto {


    @Size(max = 30, message = "Name must be at most 30 characters")
    private String name;

    @Size(max = 30, message = "Surname must be at most 30 characters")
    private String surname;

    @Email(message = "Invalid email format")
    @Size(max = 30, message = "Email must be at most 30 characters")
    private String email;

    private int minRating;

    private int maxRating;
}

