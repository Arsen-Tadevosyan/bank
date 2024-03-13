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

    @ModelAttribute("currentCard")
    public Card currentUser(@AuthenticationPrincipal SpringUser currentUser) {
        if (currentUser != null) {
            Card card = cardRepository.findByUser(currentUser.getUser());
            return card;
        }
        return null;
    }
}