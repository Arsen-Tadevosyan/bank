
package com.example.bank.service.impl;

import com.example.bank.entity.Card;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.MoneyType;
import com.example.bank.entity.enums.UserRole;
import com.example.bank.repositories.UserRepository;
import com.example.bank.service.CardService;
import com.example.bank.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final CardService cardService;

    private final SendMailService sendMailService;

    @Override
    public List<User> findByUserRole(UserRole userRole) {
        return userRepository.findByUserRole(userRole);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User register(User user, MoneyType moneyType) {
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
            sendMailService.sendWelcomeMail(user.getEmail(), "Welcome" + " " + user.getName(), user,
                    " http://localhost:8080/user/activate?token=" + user.getToken(), "welcomeMail");
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
        return userRepository.save(user);
    }

    @Override
    public User findByToken(int token) {
        return userRepository.findByToken(token);
    }
}
