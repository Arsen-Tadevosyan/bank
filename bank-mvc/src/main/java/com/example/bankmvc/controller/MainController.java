package com.example.bankmvc.controller;

import com.example.bankcommon.entity.enums.UserRole;
import com.example.bankcommon.security.CurrentUser;
import com.example.bankcommon.service.TransactionService;
import com.example.bankcommon.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class MainController {

    @Value("${picture.upload.directory}")
    private String uploadDirectory;

    private final TransactionService transactionService;
    private final UserService userService;

    @GetMapping("/")
    public String mainPage(@AuthenticationPrincipal CurrentUser currentUser, ModelMap modelMap) {
        int transactionsCount = transactionService.getCount();
        int usersCount = userService.getCount();
        modelMap.addAttribute("transactionsCount", transactionsCount);
        modelMap.addAttribute("totalUsers", usersCount);

        if (currentUser == null || currentUser.getUser().getUserRole() == UserRole.USER) {
            return "user/home";
        }
        return "admin/home";
    }

    @GetMapping(value = "getImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@RequestParam("picName") String picName) throws IOException {
        File file = new File(uploadDirectory, picName);
        if (file.exists()) {
            return IOUtils.toByteArray(new FileInputStream(file));
        }
        return null;
    }

}
