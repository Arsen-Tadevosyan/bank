package com.example.bank.service.impl;

import com.example.bank.entity.Card;
import com.example.bank.repositories.CardRepository;
import com.example.bank.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;


    @Override
    public Card save(Card card) {
        return cardRepository.save(card);
    }
}
