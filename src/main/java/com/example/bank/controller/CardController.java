package com.example.bank.controller;

import com.example.bank.entity.enums.MoneyType;
import com.example.bank.security.CurrentUser;
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
    public String balanceAdd(@RequestParam(name = "sizeAdd", required = false) String sizeAddString,
                             @RequestParam(name = "moneyType", required = false) String moneyTypeString,
                             @AuthenticationPrincipal CurrentUser currentUser) {
        if (sizeAddString == null || sizeAddString.isEmpty() || moneyTypeString == null || moneyTypeString.isEmpty()) {
            return "redirect:/balance?msg=Missing parameters";
        }
        double add;
        MoneyType moneyType;
        try {
            add = Double.parseDouble(sizeAddString);
            moneyType = MoneyType.valueOf(moneyTypeString);
        } catch (NumberFormatException e) {
            return "redirect:/balance?msg=Invalid parameter format";
        }
        boolean status = cardService.addMoney(add, moneyType, currentUser.getUser());
        if (!status) {
            return "redirect:/balance?msg=Size money is negative";
        }
        log.warn("Failed to add {} {} to {}'s card due to insufficient balance at {}", add, moneyType, currentUser.getUser().getName(), LocalDateTime.now());
        return "redirect:/balance";
    }


    @PostMapping("/balance/withdraw")
    public String withdrawMoney(@RequestParam(name = "size", required = false) String sizeString,
                                @AuthenticationPrincipal CurrentUser currentUser) {
        if (sizeString == null || sizeString.isEmpty()) {
            return "redirect:/balance?msg=Missing size parameter";
        }
        double size;
        try {
            size = Double.parseDouble(sizeString);
        } catch (NumberFormatException e) {
            return "redirect:/balance?msg=Invalid size parameter format";
        }
        if (size <= 0) {
            return "redirect:/balance?msg=Invalid size parameter value";
        }
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
