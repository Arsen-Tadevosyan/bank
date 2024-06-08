package com.example.bankcommon.service;


import com.example.bankcommon.entity.User;
import com.example.bankcommon.entity.enums.MoneyType;
import com.example.bankcommon.entity.enums.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findByEmail(String email);

    User register(User user, MoneyType moneyType);

    User findByToken(int token);

    User save(User user);

    User findRandomAdmin();

    int getCount();
}
