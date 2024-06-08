package com.example.bankcommon.service.impl;

import com.example.bankcommon.entity.Card;
import com.example.bankcommon.entity.User;
import com.example.bankcommon.entity.enums.MoneyType;
import com.example.bankcommon.entity.enums.UserRole;
import com.example.bankcommon.repositories.UserRepository;
import com.example.bankcommon.service.CardService;
import com.example.bankcommon.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CardService cardService;
    private final SendMailService sendMailService;



    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User register(User user, MoneyType moneyType) {
        log.info("Registering new user: {}", user);
        user.setUserRole(UserRole.USER);
        user.setRating(5);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saveUser = userRepository.save(user);
        String number = String.valueOf(System.currentTimeMillis());
        Random random = new Random();
        int randomNumber = random.nextInt(899999) + 100000;
        user.setToken(randomNumber);
        userRepository.save(user);
        try {
            sendMailService.sendVerificationMail(user.getEmail(), "Welcome" + " " + user.getName(), user,
                    "mail/welcomeMail");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        String cardNumber = number + randomNumber;
        Card card = Card.builder()
                .moneyType(moneyType)
                .user(saveUser)
                .number(cardNumber)
                .build();
        cardService.save(card);
        return saveUser;
    }

    @Override
    public User save(User user) {
        log.info("Saving user: {}", user);
        return userRepository.save(user);
    }

    @Override
    public User findRandomAdmin() {
        return userRepository.findRandomUserByUserRole(UserRole.ADMIN);
    }

    @Override
    public int getCount() {
        return (int) userRepository.count();
    }

    @Override
    public User findByToken(int token) {
        return userRepository.findByToken(token);
    }
}
