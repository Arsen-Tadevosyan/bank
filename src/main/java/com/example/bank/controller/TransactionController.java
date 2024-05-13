package com.example.bank.controller;



import com.example.bank.entity.Card;
import com.example.bank.entity.Repay;
import com.example.bank.entity.Transaction;
import com.example.bank.entity.User;
import com.example.bank.entity.Notification;

import com.example.bank.entity.enums.NotificationType;
import com.example.bank.entity.enums.Status;
import com.example.bank.entity.enums.StatusRepay;
import com.example.bank.dto.CreditRequestDto;

import com.example.bank.entity.Card;
import com.example.bank.entity.Repay;
import com.example.bank.entity.Transaction;
import com.example.bank.entity.User;
import com.example.bank.entity.Notification;

import com.example.bank.entity.enums.NotificationType;
import com.example.bank.entity.enums.Status;
import com.example.bank.entity.enums.TransactionType;
import com.example.bank.security.CurrentUser;

import com.example.bank.service.CardService;
import com.example.bank.service.NotificationService;
import com.example.bank.service.RepayService;
import com.example.bank.service.TransactionService;
import com.example.bank.service.TransferService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final CardService cardService;
    private final TransactionService transactionService;
    private final RepayService repayService;
    private final NotificationService notificationService;
    private final TransferService transferService;

    @GetMapping("/transaction/transfer")
    public String transferMoneyPage(@RequestParam(value = "msg", required = false) String msg, ModelMap modelMap) {
        if (msg != null) {
            modelMap.addAttribute("msg", msg);
        }
        return "user/transfer";
    }

    @PostMapping("/transaction/transfer")
    public String transferMoney(@RequestParam(name = "size", required = false) String sizeString,
                                @RequestParam(name = "cardNumber", required = false) String cardNumber,
                                @AuthenticationPrincipal CurrentUser currentUser,
                                RedirectAttributes redirectAttributes) {
        if (StringUtils.isBlank(sizeString) || StringUtils.isBlank(cardNumber)) {
            redirectAttributes.addFlashAttribute("msg", "Missing parameters");
            return "redirect:/transaction/transfer";
        }
        double size;
        try {
            size = Double.parseDouble(sizeString);
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("msg", "Invalid parameter format");
            return "redirect:/transaction/transfer";
        }
        boolean status = cardService.transfer(size, cardNumber, currentUser.getUser());
        if (!status) {
            log.warn("Not enough money on the card for transfer. Card Number: {}", cardNumber);
            redirectAttributes.addFlashAttribute("msg", "There is not enough money on your card or the card does not exist");
        } else {
            log.info("Money successfully transferred from card: {}", cardNumber);
        }
        return "redirect:/transaction/transfer";
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
    public String personalCredit(@AuthenticationPrincipal CurrentUser currentUser,
                                 @Validated CreditRequestDto request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessage.append(error.getDefaultMessage()).append(" ");
            }
            redirectAttributes.addAttribute("msg", errorMessage.toString());
            return "redirect:/transaction/personalCredit";
        }
        double size = request.getSizeMoney();
        int months = request.getMonths();
        User user = currentUser.getUser();
        if (user.getRating() == 0) {
            redirectAttributes.addAttribute("msg", "Your request was declined due to your credit history");
            return "redirect:/transaction/personalCredit";
        }
        Transaction transaction = transactionService.save(size, months, user, TransactionType.PERSONAL);
        if (transaction == null) {
            redirectAttributes.addAttribute("msg", "Your request was declined");
            return "redirect:/transaction/personalCredit";
        }
        repayService.save(transaction);
        redirectAttributes.addAttribute("msg", "Your transaction has been confirmed");
        return "redirect:/transaction/personalCredit";
    }


    @GetMapping("/transaction/educationCredit")
    public String educationCredit(@AuthenticationPrincipal CurrentUser currentUser,
                                  ModelMap modelMap) {
        modelMap.addAttribute("currentUser", currentUser.getUser());
        return "user/educationCredit";
    }

    @PostMapping("/transaction/educationCredit")
    public String EducationCredit(@AuthenticationPrincipal CurrentUser currentUser,
                                  @Validated CreditRequestDto request,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessage.append(error.getDefaultMessage()).append(" ");
            }
            redirectAttributes.addAttribute("msg", errorMessage.toString());
            return "redirect:/transaction/personalCredit";
        }
        double size;
        int months;
        try {
            size = request.getSizeMoney();
            months = request.getMonths();
        } catch (NumberFormatException e) {
            redirectAttributes.addAttribute("msg", "Invalid parameter format");
            return "redirect:/transaction/personalCredit";
        }
        User user = currentUser.getUser();
        if (user.getRating() == 0) {
            redirectAttributes.addAttribute("msg", "Your request was declined due to your credit history");
            return "redirect:/transaction/personalCredit";
        }
        Transaction transaction = transactionService.save(size, months, user, TransactionType.EDUCATION);
        if (transaction == null) {
            redirectAttributes.addAttribute("msg", "Your request was declined");
            return "redirect:/transaction/personalCredit";
        }

        repayService.save(transaction);
        redirectAttributes.addAttribute("msg", "Your transaction has been confirmed");
        return "redirect:/transaction/personalCredit";
    }


    @GetMapping("/transaction/businessCredit")
    public String businessCredit(@AuthenticationPrincipal CurrentUser currentUser,
                                 ModelMap modelMap) {
        modelMap.addAttribute("currentUser", currentUser.getUser());
        return "user/businessCredit";
    }

    @PostMapping("/transaction/businessCredit")
    public String BusinessCredit(@AuthenticationPrincipal CurrentUser currentUser,
                                 @Validated CreditRequestDto request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessage.append(error.getDefaultMessage()).append(" ");
            }
            redirectAttributes.addAttribute("msg", errorMessage.toString());
            return "redirect:/transaction/personalCredit";
        }
        double size;
        int months;
        try {
            size = request.getSizeMoney();
            months = request.getMonths();
        } catch (NumberFormatException e) {
            redirectAttributes.addAttribute("msg", "Invalid parameter format");
            return "redirect:/transaction/personalCredit";
        }
        User user = currentUser.getUser();
        if (user.getRating() == 0) {
            redirectAttributes.addAttribute("msg", "Your request was declined due to your credit history");
            return "redirect:/transaction/personalCredit";
        }
        Transaction transaction = transactionService.save(size, months, user, TransactionType.BUSINESS);
        if (transaction == null) {
            redirectAttributes.addAttribute("msg", "Your request was declined");
            return "redirect:/transaction/personalCredit";
        }
        repayService.save(transaction);
        redirectAttributes.addAttribute("msg", "Your transaction has been confirmed");
        return "redirect:/transaction/personalCredit";
    }

    @GetMapping("/myCredits")
    public String MyTransactions(@AuthenticationPrincipal CurrentUser currentUser,
                                 @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                 @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                 @RequestParam(value = "msg", required = false) String msg,
                                 ModelMap modelMap) {
        if (msg != null) {
            modelMap.addAttribute("msg", msg);
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Transaction> transactions = transactionService.findByUser(currentUser.getUser(), pageable);
        modelMap.addAttribute("transactions", transactions);
        int totalPages = transactions.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            modelMap.addAttribute("pageNumbers", pageNumbers);
        }
        return "user/myCredits";
    }

    @GetMapping("creditSinglePage/{id}")
    public String creditSinglePage(@PathVariable("id") int id,
                                   @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                   @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                   ModelMap modelMap,
                                   @RequestParam(value = "msg", required = false) String msg) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (msg != null) {
            modelMap.addAttribute("msg", msg);
        }
        Transaction transaction = transactionService.getById(id);
        Page<Repay> repays = repayService.getByTransaction(transaction, pageable);
        modelMap.addAttribute("transaction", transaction);
        modelMap.addAttribute("repays", repays);
        return "user/creditSinglePage";
    }

    @PostMapping("/repay")
    public String Repay(@AuthenticationPrincipal CurrentUser currentUser,
                        @RequestParam(name = "repayId", required = false) String id,
                        RedirectAttributes redirectAttributes) {
        if (StringUtils.isBlank(id)) {
            redirectAttributes.addFlashAttribute("msg", "Invalid or missing repayId");
            log.warn("Repay method called with invalid or missing repayId");
            return "redirect:/myCredits";
        }
        Integer repayId;
        try {
            repayId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("msg", "Invalid format for repayId");
            log.warn("Repay method called with invalid repayId format");
            return "redirect:/myCredits";
        }
        log.info("Repay method called by user: {} for repayId: {}", currentUser.getUser().getEmail(), repayId);
        Optional<Repay> serviceById = repayService.getById(repayId);
        if (serviceById.isEmpty()) {
            redirectAttributes.addFlashAttribute("msg", "You don't have a repayment with that id");
            log.warn("Repayment with id {} not found for user {}", repayId, currentUser.getUser().getEmail());
            return "redirect:/myCredits";
        }
        Repay repay = serviceById.get();
        if (!repay.getTransaction().getUser().equals(currentUser.getUser())) {
            redirectAttributes.addFlashAttribute("msg", "You don't have a repayment with that id");
            log.warn("Repayment with id {} does not belong to user {}", repayId, currentUser.getUser().getEmail());
            return "redirect:/myCredits";
        }
        Card card = cardService.gatByUser(currentUser.getUser());
        boolean status = repayService.repay(repayId, card);
        if (!status) {
            redirectAttributes.addFlashAttribute("msg", "Your balance is insufficient or you have already paid with that ID");
            log.warn("Failed to repay with id {} due to insufficient balance or already paid", repayId);
            return "redirect:/creditSinglePage/" + repay.getTransaction().getId();
        }
        log.info("Repayment with id {} successful", repayId);
        return "redirect:/creditSinglePage/" + repay.getTransaction().getId();
    }


    @GetMapping("/user/transactions")
    public String transactions(@AuthenticationPrincipal CurrentUser currentUser, ModelMap modelMap,
                               @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                               @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        modelMap.addAttribute("transactions", transactionService.findByUser(currentUser.getUser(), pageable));
        return "/user/transactionHistory";
    }

    @GetMapping("/user/transfers")
    public String transfers(@AuthenticationPrincipal CurrentUser currentUser, ModelMap modelMap,
                            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                            @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        modelMap.addAttribute("transfers", transferService.getTransfersForUser(currentUser.getUser(), pageable));
        return "/user/transfersHistory";

    }

    @GetMapping("/user/repays")
    public String Repays(@AuthenticationPrincipal CurrentUser currentUser, ModelMap modelMap,
                         @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                         @RequestParam(value = "size", defaultValue = "20", required = false) int size) {
        if (size > 20) {
            return "redirect:/user/repays?page=" + page + "&size=20";
        }
        List<Transaction> byUser = transactionService.getByUser(currentUser.getUser());
        List<Repay> repays = new ArrayList<>();
        if (byUser.isEmpty()) {
            return "redirect:/myCredits?msg=You don't have any repays";
        }
        for (Transaction transaction : byUser) {
            List<Repay> byTransactionAndStatus = repayService.findByTransaction(transaction);
            repays.addAll(byTransactionAndStatus);
        }
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, repays.size());
        List<Repay> pageRepayList = repays.subList(startIndex, endIndex);
        Page<Repay> repayPage = new PageImpl<>(pageRepayList, PageRequest.of(page - 1, size), repays.size());
        modelMap.addAttribute("repays", repayPage);
        return "/user/repaysHistory";
    }

    @GetMapping("/transaction/deposit")
    public String depositPage(@RequestParam(value = "msg", required = false) String msg, ModelMap modelMap) {
        if (msg != null) {
            modelMap.addAttribute("msg", msg);
        }
        return "/user/deposit";
    }

    @PostMapping("/transaction/deposit")
    public String deposit(@AuthenticationPrincipal CurrentUser currentUser,
                          @RequestParam(value = "sizeMoney", required = false) double size,
                          @RequestParam(value = "months", required = false) Integer months) {
        Transaction transaction;
        if (months == null) {
            transaction = transactionService.saveFreeTimeDeposit(currentUser.getUser(), size);
        } else {
            transaction = transactionService.saveDeposit(currentUser.getUser(), size, months);
        }
        if (transaction == null) {
            return "redirect:/transaction/deposit?msg=Your request was declined";
        }
        return "redirect:/transaction/deposit?msg=Your request confirmed";
    }

    @GetMapping("/transaction/getDeposit/{id}")
    public String getDeposit(@PathVariable("id") int id, @AuthenticationPrincipal CurrentUser currentUser) {
        User user = currentUser.getUser();
        Transaction transaction = transactionService.getById(id);
        if (!user.equals(transaction.getUser())) {
            return "redirect:/transaction/deposit?msg=You don't have the right user";
        }
        if (transaction.getStatus() == Status.DURING) {
            if (!transaction.getIssueDate().equals(transaction.getFinishDate())) {
                if (transaction.getFinishDate().isBefore(LocalDate.now())) {
                    Card card = cardService.gatByUser(user);
                    double addMoney = transaction.getRemainingMoney() + card.getBalance();
                    card.setBalance(addMoney);
                    transaction.setStatus(Status.FINISHED);
                    notificationService.save(Notification.builder()
                            .notificationType(NotificationType.INFO)
                            .user(user)
                            .dateDispatch(LocalDateTime.now())
                            .message("   your deposit has been successfully completed")
                            .build());
                }
            }
            if (transaction.getIssueDate().equals(transaction.getFinishDate())) {
                Card card = cardService.gatByUser(user);
                double addMoney = transaction.getRemainingMoney() + card.getBalance();
                card.setBalance(addMoney);
                transaction.setStatus(Status.FINISHED);
                notificationService.save(Notification.builder()
                        .notificationType(NotificationType.INFO)
                        .user(user)
                        .dateDispatch(LocalDateTime.now())
                        .message("   your deposit has been successfully completed")
                        .build());
            }
        }
        return "redirect:/myCredits";
    }

}
