package com.example.bank.controller;

import com.example.bank.entity.User;
import com.example.bank.entity.enums.MoneyType;
import com.example.bank.entity.enums.UserRole;
import com.example.bank.security.CurrentUser;
import com.example.bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/register")
    public String registerPage() {
        return "user/register";
    }

    @PostMapping("/user/register")
    public String register(@ModelAttribute User user,
                           @RequestParam("moneyType") MoneyType moneyType) {
        if (userService.findByEmail(user.getEmail()) != null) {
            userService.register(user,moneyType);
            return "redirect:/user/register";
        } else {
            return "redirect:/user/register";
        }
    }

    @GetMapping("/user/login")
    public String loginPage(){
        return "/user/login";
    }

    @GetMapping("/loginSuccessUrl")
    public String loginSuccess(@AuthenticationPrincipal CurrentUser currentUser){
        User user = currentUser.getUser();
        if (user.getUserRole() == UserRole.USER){
            return "redirect:/";
        }
        //AdminPage
        return "/";
    }

}
