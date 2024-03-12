package com.example.bank.controller;

import com.example.bank.entity.Card;
import com.example.bank.repositories.CardRepository;
import com.example.bank.security.SpringUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class CardControllerAdvice {

    private final CardRepository cardRepository;

    @ModelAttribute("currentUserCard")
    public Card currentUser(@AuthenticationPrincipal SpringUser springUser) {
        if (springUser != null) {
            Card card = cardRepository.findByUser(springUser.getUser());
            return card;
        }
        return null;
    }
}