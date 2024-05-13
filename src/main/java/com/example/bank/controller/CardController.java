package com.example.bank.controller;

import com.example.bank.entity.AddWithdraw;
import com.example.bank.entity.Card;
import com.example.bank.entity.enums.MoneyType;
import com.example.bank.entity.enums.StatusAddWithdraw;
import com.example.bank.security.CurrentUser;
import com.example.bank.service.CardService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                             @AuthenticationPrincipal CurrentUser currentUser,
                             RedirectAttributes redirectAttributes) {
        if (StringUtils.isBlank(sizeAddString) || StringUtils.isBlank(moneyTypeString)) {
            redirectAttributes.addFlashAttribute("msg", "Missing parameters");
            return "redirect:/balance";
        }
        double add;
        MoneyType moneyType;
        try {
            add = Double.parseDouble(sizeAddString);
            moneyType = MoneyType.valueOf(moneyTypeString);
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("msg", "Invalid parameter format");
            return "redirect:/balance";
        }
        if (add < 0) {
            redirectAttributes.addFlashAttribute("msg", "Size money is negative");
            return "redirect:/balance";
        }
        boolean status = cardService.addMoney(add, moneyType, currentUser.getUser());
        if (!status) {
            log.warn("Failed to add {} {} to {}'s card due to insufficient balance at {}", add, moneyType, currentUser.getUser().getName(), LocalDateTime.now());
        } else {
            cardService.saveAddWithdraw(AddWithdraw.builder()
                    .size(add)
                    .moneyType(moneyType)
                    .status(StatusAddWithdraw.ADD)
                    .dateTime(LocalDateTime.now())
                    .user(currentUser.getUser())
                    .build());
        }
        return "redirect:/balance";
    }


    @PostMapping("/balance/withdraw")
    public String withdrawMoney(@RequestParam(name = "size", required = false) String sizeString,
                                @AuthenticationPrincipal CurrentUser currentUser,
                                RedirectAttributes redirectAttributes) {
        if (StringUtils.isBlank(sizeString)) {
            redirectAttributes.addFlashAttribute("msg", "Missing size parameter");
            return "redirect:/balance";
        }
        double size;
        try {
            size = Double.parseDouble(sizeString);
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("msg", "Invalid size parameter format");
            return "redirect:/balance";
        }
        if (size <= 0) {
            redirectAttributes.addFlashAttribute("msg", "Invalid size parameter value");
            return "redirect:/balance";
        }
        boolean success = cardService.withdrawMoney(size, currentUser.getUser());
        if (!success) {
            log.warn("Failed to withdraw {} from {}'s card due to insufficient balance at {}", size, currentUser.getUser().getName(), LocalDateTime.now());
            redirectAttributes.addFlashAttribute("msg", "Insufficient balance to withdraw money");
            return "redirect:/balance";
        }
        Card card = cardService.gatByUser(currentUser.getUser());
        cardService.saveAddWithdraw(AddWithdraw.builder()
                .size(size)
                .moneyType(card.getMoneyType())
                .status(StatusAddWithdraw.WITHDRAW)
                .dateTime(LocalDateTime.now())
                .user(currentUser.getUser())
                .build());
        logTransaction(currentUser.getUser().getName(), "withdrawn", size, null);
        return "redirect:/balance";
    }

    private void logTransaction(String username, String action, double amount, MoneyType moneyType) {
        String transactionType = (moneyType != null) ? moneyType.name() : "";
        log.info("{} {} {} {} from their card at {}", username, action, amount, transactionType, LocalDateTime.now());
    }


    @GetMapping("/user/addWithdraw")
    public String AddWithdraw(@AuthenticationPrincipal CurrentUser currentUser, ModelMap modelMap,
                              @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                              @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        modelMap.addAttribute("addWithdraws", cardService.gatByUser(currentUser.getUser(), pageable));
        {
            return "/user/addWithdrawHistory";
        }
    }
}
