package com.example.bank.controller;

import com.example.bank.entity.User;
import com.example.bank.entity.enums.MoneyType;
import com.example.bank.entity.enums.UserRole;
import com.example.bank.repositories.UserRepository;
import com.example.bank.security.SpringUser;
import com.example.bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    public String loginSuccess(@AuthenticationPrincipal SpringUser springUser){
        User user = springUser.getUser();
        if (user.getUserRole() == UserRole.USER){
            System.out.println("xz");
            return "redirect:/";
        }
        //AdminPage
        return "/";
    }

}
