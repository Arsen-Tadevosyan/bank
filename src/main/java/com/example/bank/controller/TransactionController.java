package com.example.bank.controller;

import com.example.bank.entity.Card;
import com.example.bank.entity.Repay;
import com.example.bank.entity.Transaction;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.TransactionType;
import com.example.bank.security.CurrentUser;
import com.example.bank.service.CardService;
import com.example.bank.service.NotificationService;
import com.example.bank.service.RepayService;
import com.example.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final CardService cardService;
    private final TransactionService transactionService;
    private final RepayService repayService;
    private final NotificationService notificationService;

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
                                @AuthenticationPrincipal CurrentUser currentUser) {
        boolean status = cardService.transfer(size, cardNumber, currentUser.getUser());
        if (status == false) {
            log.warn("Not enough money on the card for transfer. Card Number: {}", cardNumber);
            return "redirect:/transaction/transfer?msg=There is not enough money on your card or card dose not exist";
        } else {
            log.info("Money successfully transferred from card: {}", cardNumber);
            return "redirect:/transaction/transfer";
        }
    }

    @GetMapping("/transaction/aboutPersonalCredit")
    public String aboutPersonalCredit() {
        return "user/aboutPersonalCredit";
    }

    @GetMapping("/transaction/aboutEducationCredit")
    public String aboutEducationCredit() {
        return "user/aboutEducationCredit";
    }

    @GetMapping("/transaction/aboutBusinessCredit")
    public String aboutBusinessCredit() {
        return "user/aboutBusinessCredit";
    }

    @GetMapping("/transaction/personalCredit")
    public String personalCredit(@RequestParam(value = "msg", required = false) String msg,
                                 @AuthenticationPrincipal CurrentUser currentUser,
                                 ModelMap modelMap) {
        if (msg != null) {
            modelMap.addAttribute("msg", msg);
        }
        modelMap.addAttribute("currentUser", currentUser.getUser());
        return "user/personalCredit";
    }

    @PostMapping("/transaction/personalCredit")
    public String PersonalCredit(@AuthenticationPrincipal CurrentUser currentUser,
                                 @RequestParam("sizeMoney") double size,
                                 @RequestParam("months") int mounts) {
        User user = currentUser.getUser();
        if (user.getRating() == 0) {
            return "redirect:/transaction/personalCredit?msg=Your request was declined due to your credit history";
        }
        Transaction transaction = transactionService.save(size, mounts, user, TransactionType.PERSONAL);
        if (transaction == null) {
            return "redirect:/transaction/personalCredit?msg=Your request was declined";
        }
        repayService.save(transaction);
        return "redirect:/transaction/personalCredit?msg=Your transaction has been confirmed";
    }

    @GetMapping("/transaction/educationCredit")
    public String educationCredit(@AuthenticationPrincipal CurrentUser currentUser,
                                  ModelMap modelMap) {
        modelMap.addAttribute("currentUser", currentUser.getUser());
        return "user/educationCredit";
    }

    @PostMapping("/transaction/educationCredit")
    public String EducationCredit(@AuthenticationPrincipal CurrentUser currentUser,
                                  @RequestParam("sizeMoney") double size,
                                  @RequestParam("months") int mounts) {
        User user = currentUser.getUser();
        if (user.getRating() == 0) {
            return "redirect:/transaction/personalCredit?msg=Your request was declined due to your credit history";
        }
        Transaction transaction = transactionService.save(size, mounts, user, TransactionType.EDUCATION);
        if (transaction == null) {
            return "redirect:/transaction/personalCredit?msg=Your request was declined";
        }
        repayService.save(transaction);
        return "redirect:/transaction/personalCredit?msg=Your transaction has been confirmed";
    }

    @GetMapping("/transaction/businessCredit")
    public String businessCredit(@AuthenticationPrincipal CurrentUser currentUser,
                                 ModelMap modelMap) {
        modelMap.addAttribute("currentUser", currentUser.getUser());
        return "user/businessCredit";
    }

    @PostMapping("/transaction/businessCredit")
    public String BusinessCredit(@AuthenticationPrincipal CurrentUser currentUser,
                                 @RequestParam("sizeMoney") double size,
                                 @RequestParam("months") int mounts) {
        User user = currentUser.getUser();
        if (user.getRating() == 0) {
            return "redirect:/transaction/personalCredit?msg=Your request was declined due to your credit history";
        }
        Transaction transaction = transactionService.save(size, mounts, user, TransactionType.BUSINESS);
        if (transaction == null) {
            return "redirect:/transaction/personalCredit?msg=Your request was declined";
        }
        repayService.save(transaction);
        return "redirect:/transaction/personalCredit?msg=Your transaction has been confirmed";
    }

    @GetMapping("/myCredits")
    public String MyTransactions(@AuthenticationPrincipal CurrentUser currentUser,
                                 ModelMap modelMap) {
        List<Transaction> transactions = transactionService.findByUser(currentUser.getUser());
        modelMap.addAttribute("transactions", transactions);
        return "user/myCredits";
    }

    @GetMapping("creditSinglePage/{id}")
    public String creditSinglePage(@PathVariable("id") int id,
                                   ModelMap modelMap,
                                   @RequestParam(value = "msg", required = false) String msg) {
        if (msg != null) {
            modelMap.addAttribute("msg", msg);
        }
        Transaction transaction = transactionService.getById(id);
        List<Repay> repays = repayService.getByTransaction(transaction);
        modelMap.addAttribute("transaction", transaction);
        modelMap.addAttribute("repays", repays);
        return "user/creditSinglePage";
    }

    @PostMapping("/repay")
    public String Repay(@AuthenticationPrincipal CurrentUser currentUser,
                        @RequestParam("repayId") int id) {
        Optional<Repay> serviceById = repayService.getById(id);
        Repay repay = null;
        if (serviceById.isEmpty()) {
            return "redirect:/myCredits/?msg=You don't have repay with id";
        }
        repay = serviceById.get();
        if (!repay.getTransaction().getUser().equals(currentUser.getUser())) {
            return "redirect:/myCredits/?msg=You don't have repay with id";
        }
        Card card = cardService.gatByUser(currentUser.getUser());
        boolean status = repayService.repay(id, card);
        log.info("");
        if (!status) {
            return "redirect:/creditSinglePage/" + repay.getTransaction().getId() + "?msg=your balance is insufficient or you have already paid with that ID";
        }
        log.info("");
        return "redirect:/creditSinglePage/" + repay.getTransaction().getId();
    }

}
