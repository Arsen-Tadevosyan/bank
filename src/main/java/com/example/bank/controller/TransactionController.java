package com.example.bank.controller;

import com.example.bank.security.SpringUser;
import com.example.bank.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final CardService cardService;

    @GetMapping("/transaction/transfer")
    public String transferMoneyPage(@RequestParam(value = "msg", required = false) String msg, ModelMap modelMap) {
        if (msg != null) {
            modelMap.addAttribute("msg", msg);
        }
        return "user/transfer";
    }

    @PostMapping("/transaction/transfer")
    public String transferMoney(@RequestParam("size") double size,
                                @RequestParam("cardNumber") String cardNumber,
                                @AuthenticationPrincipal SpringUser currentUser) {
        boolean status = cardService.transfer(size, cardNumber, currentUser);
        if (status == false) {
            log.warn("Not enough money on the card for transfer. Card Number: {}", cardNumber);
            return "redirect:/transaction/transfer?msg=There is not enough money on your card or card dose not exist";
        } else {
            log.info("Money successfully transferred from card: {}", cardNumber);
            return "redirect:/transaction/transfer";
        }
    }

}
