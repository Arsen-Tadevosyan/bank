package com.example.bank.controller;

import com.example.bank.entity.ChatRoom;
import com.example.bank.entity.Message;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.MoneyType;
import com.example.bank.entity.enums.UserRole;
import com.example.bank.security.CurrentUser;
import com.example.bank.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final ChatRoomService chatRoomService;
    private final UserService userService;
    private final MessageService messageService;

    private final CardService cardService;

    private final TransferService transferService;

    private final RepayService repayService;

    private final TransactionService transactionService;

    @GetMapping("/user/register")
    public String registerPage(@RequestParam(value = "msg", required = false) String msg, ModelMap modelMap) {
        if (msg != null && !msg.isEmpty()) {
            modelMap.addAttribute("msg", msg);
        }
        return "user/register";
    }


    @PostMapping("/user/register")
    public String register(@RequestParam(value = "moneyType", required = false) String moneyTypeString,
                           @RequestParam(value = "name", required = false) String name,
                           @RequestParam(value = "surname", required = false) String surname,
                           @RequestParam(value = "email", required = false) String email,
                           @RequestParam(value = "password", required = false) String password,
                           @RequestParam(value = "age", required = false) String stringAge,
                           @RequestParam(value = "phone", required = false) String phone) {
        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return "redirect:/user/register?msg=Missing required fields";
        }
        if (!isValidEmail(email)) {
            return "redirect:/user/register?msg=Invalid email format";
        }
        MoneyType moneyType;
        try {
            moneyType = MoneyType.valueOf(moneyTypeString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return "redirect:/user/register?msg=Invalid money type";
        }
        if (phone == null && phone.isEmpty()) {
            return "redirect:/user/register?msg=Phone Is Empty";
        }
        int age;
        try {
            age = Integer.parseInt(stringAge);
        } catch (NumberFormatException e) {
            return "redirect:/user/register?msg=Invalid Parameter Format";
        }
        if (age < 18) {
            return "redirect:/user/register?msg=Age Is Young";
        }
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setPassword(password);
        user.setAge(age);
        user.setPhone(phone);


        if (userService.findByEmail(email).isEmpty()) {
            userService.register(user, moneyType);
            return "redirect:/user/verification";
        } else {
            return "redirect:/user/register?msg=Incorrect Input";
        }

    }

    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @GetMapping("/user/login")
    public String loginPage(@RequestParam(value = "msg", required = false) String msg, ModelMap modelMap) {
        if (msg != null && !msg.isEmpty()) {
            modelMap.addAttribute("msg", msg);
        }
        return "/user/login";
    }

    @GetMapping("/loginSuccessUrl")
    public String loginSuccess(@AuthenticationPrincipal CurrentUser currentUser) {
        User user = currentUser.getUser();
        if (!userService.findByEmail(user.getEmail()).isEmpty()) {
            if (user.getUserRole() == UserRole.USER) {
                return "redirect:/";
            }
            return "redirect:/admin/home";
        }
        return "redirect:/user/login?msg=Login Success";
    }

    @GetMapping("/user/profile")
    public String profilePage(@AuthenticationPrincipal CurrentUser currentUser, ModelMap modelMap) {
        modelMap.addAttribute("user", currentUser.getUser());
        modelMap.addAttribute("card", cardService.gatByUser(currentUser.getUser()));
        return "/user/profile";
    }

    @GetMapping("/contactUs")
    public String contactUsPage(@AuthenticationPrincipal CurrentUser currentUser, ModelMap modelMap) {
        ChatRoom chatRoom = chatRoomService.getByUser(currentUser.getUser());
        List<Message> messages = messageService.getByChatRoom(chatRoom);
        modelMap.addAttribute("messages", messages);
        return "user/contactUs";
    }


    @GetMapping("/user/verification")
    public String verifyPage(@RequestParam(name = "msg", required = false) String msg, ModelMap modelMap) {
        if (msg != null && !msg.isEmpty()) {
            modelMap.addAttribute("msg", msg);
        }
        return "/user/verification";
    }

    @PostMapping("/user/verification")
    public String verify(@RequestParam(name = "token", required = false) String stringToken) {
        if (stringToken == null || stringToken == "") {
            return "redirect:/user/verification?msg=Invalid Token";
        }
        int token;
        try {
            token = Integer.parseInt(stringToken);
        } catch (NumberFormatException e) {
            return "redirect:/user/verification?msg=Invalid Parameter Format";
        }
        if (!(token < 999999) || !(token > 100000)) {
            return "redirect:/user/verification?msg=Invalid Parameter";
        }
        User user = userService.findByToken(token);
        user.setActive(true);
        user.setToken(0);
        userService.save(user);
        return "redirect:/user/verification?msg=Verification Successes You Can Login";
    }

    @GetMapping("/user/history")
    public String historyPage() {
        return "/user/history";
    }
}