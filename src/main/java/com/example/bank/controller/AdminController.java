package com.example.bank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminController {

    @GetMapping("/admin/transactions")
    public String Transactions() {
        return "admin/transactions";
    }
}
