package com.example.bank.service;


import com.example.bank.entity.User;
import com.example.bank.entity.enums.MoneyType;
import com.example.bank.entity.enums.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findByUserRole(UserRole userRole);

    Optional<User> findByEmail(String email);

    User register(User user, MoneyType moneyType);

    User findByToken(int token);

    User save(User user);

    User findRandomAdmin();
}
