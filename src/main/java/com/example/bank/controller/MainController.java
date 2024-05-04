package com.example.bank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String mainPage() {
        return "user/home";
    }

    @GetMapping("/admin/home")
    public String mainPageForAdmin() {
        return "redirect:/admin/transactions";
    }
}
