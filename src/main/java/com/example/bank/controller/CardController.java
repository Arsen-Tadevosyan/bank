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
                             @AuthenticationPrincipal SpringUser springUser) {
        boolean status = cardService.addMoney(add, moneyType, springUser.getUser());
        if (status == false) {
            return "redirect:/balance?msg=Size money is minus";
        }
        log.info("{} money added  her card {} | {}",springUser.getUser().getName(),add,moneyType.name());
        return "redirect:/balance";
    }

    @PostMapping("/balance/withdraw")
    public String withdrawMoney(@RequestParam("size") double size,
                                @AuthenticationPrincipal SpringUser springUser) {
        boolean status = cardService.withdrawMoney(size, springUser.getUser());
        if (status == false) {
            log.warn("{} tried to withdraw money {} but failed",springUser.getUser().getName(),size);

            return "redirect:/balance?msg=There is not enough money on the card that you want to withdraw";
        }
        log.info("{} money withdraw  her card {}",springUser.getUser().getName(),size);
        return "redirect:/balance";
    }
}
