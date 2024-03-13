package com.example.bank.controller;

import com.example.bank.entity.enums.MoneyType;
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

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CardController {

    private final CardService cardService;

    @GetMapping("/balance")
    public String balancePage(@RequestParam(value = "msg", required = false) String msg, ModelMap modelMap) {
        if (msg != null) {
            modelMap.addAttribute("msg", msg);
        }
        return "user/cardBalance";
    }

    @PostMapping("/balance/add")
    public String balanceAdd(@RequestParam("sizeAdd") double add,
                             @RequestParam("moneyType") MoneyType moneyType,
                             @AuthenticationPrincipal SpringUser currentUser) {
        boolean status = cardService.addMoney(add, moneyType, currentUser.getUser());
        if (status == false) {
            return "redirect:/balance?msg=Size money is minus";
        }
        log.warn("Failed to add {} {} to {}'s card due to insufficient balance at {}", add, moneyType, currentUser.getUser().getName(), LocalDateTime.now());
        return "redirect:/balance";
    }

    @PostMapping("/balance/withdraw")
    public String withdrawMoney(@RequestParam("size") double size,
                                @AuthenticationPrincipal SpringUser currentUser) {
        boolean success = cardService.withdrawMoney(size, currentUser.getUser());
        if (success) {
            logTransaction(currentUser.getUser().getName(), "withdrawn", size, null);
        } else {
            log.warn("Failed to withdraw {} from {}'s card due to insufficient balance at {}", size, currentUser.getUser().getName(), LocalDateTime.now());
            return "redirect:/balance?msg=Insufficient balance to withdraw money";
        }
        return "redirect:/balance";
    }
    private void logTransaction(String username, String action, double amount, MoneyType moneyType) {
        String transactionType = (moneyType != null) ? moneyType.name() : "";
        log.info("{} {} {} {} from their card at {}", username, action, amount, transactionType, LocalDateTime.now());
    }
}
