package com.example.bankcommon.dto;

import com.example.bankcommon.entity.enums.Gender;
import com.example.bankcommon.entity.enums.MoneyType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRegisterDto {


    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String surname;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @Min(value = 18, message = "Age must be at least 18")
    @NotNull(message = "Age is required")
    private Integer age;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotNull
    private Gender gender;

    @NotNull
    private MoneyType moneyType;

    private MultipartFile picture;

}
