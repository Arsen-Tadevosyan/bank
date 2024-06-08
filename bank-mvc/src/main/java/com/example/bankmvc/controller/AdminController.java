package com.example.bankmvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/transactions")
    public String Transactions() {
        return "admin/transactions";
    }

    @GetMapping("/users")
    public String Users() {
        return "admin/users";
    }

    @GetMapping("/transfers")
    public String showCreditList(){
        return "admin/transfers";
    }
}
