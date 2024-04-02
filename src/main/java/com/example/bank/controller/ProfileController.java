package com.example.bank.controller;


import com.example.bank.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final CardControllerAdvice cardControllerAdvice;

    @GetMapping("/user/profile")
    public String profilePage(@AuthenticationPrincipal CurrentUser currentUser, ModelMap modelMap) {
        modelMap.addAttribute("user", currentUser.getUser());
        modelMap.addAttribute("card", cardControllerAdvice.currentUser(currentUser));
        return "/user/profile";
    }

}
