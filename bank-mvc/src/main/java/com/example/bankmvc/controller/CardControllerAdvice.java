package com.example.bankmvc.controller;

import com.example.bankcommon.entity.Card;
import com.example.bankcommon.repositories.CardRepository;
import com.example.bankcommon.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class CardControllerAdvice {

    private final CardRepository cardRepository;

    @ModelAttribute("currentCard")
    public Card currentUser(@AuthenticationPrincipal CurrentUser currentUser) {
        if (currentUser != null) {
            Card card = cardRepository.findByUser(currentUser.getUser());
            return card;
        }
        return null;
    }
}