package com.example.bank.controller;

import com.example.bank.entity.User;
import com.example.bank.entity.enums.MoneyType;
import com.example.bank.entity.enums.UserRole;
import com.example.bank.security.CurrentUser;
import com.example.bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/register")
    public String registerPage(@RequestParam(value = "msg",required = false) String msg, ModelMap modelMap) {
        if (msg != null && !msg.isEmpty()){
            modelMap.addAttribute("msg", msg);
        }
        return "user/register";
    }

    @PostMapping("/user/register")
    public String register(@ModelAttribute User user,
                           @RequestParam("moneyType") MoneyType moneyType) {
        if (userService.findByEmail(user.getEmail()).isEmpty()) {
            userService.register(user,moneyType);
            return "redirect:/user/register?msg=Registration successful";
        } else {
            return "redirect:/user/register?msg=Email already in use";
        }
    }

    @GetMapping("/user/login")
    public String loginPage(@RequestParam(value = "msg",required = false) String msg,ModelMap modelMap) {
        if (msg != null && !msg.isEmpty()){
            modelMap.addAttribute("msg", msg);
        }
        return "/user/login";
    }

    @GetMapping("/loginSuccessUrl")
    public String loginSuccess(@AuthenticationPrincipal CurrentUser currentUser){
        User user = currentUser.getUser();
        if (!userService.findByEmail(user.getEmail()).isPresent()){
            if (user.getUserRole() == UserRole.USER){
                return "redirect:/";
            }
            //AdminPage
            return "/";
        }
        return "redirect:/user/login?msg=Email or password is incorrect";
    }

}
