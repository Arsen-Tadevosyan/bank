package com.example.bank.controller;

import com.example.bank.entity.ChatRoom;
import com.example.bank.entity.Message;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.MoneyType;
import com.example.bank.entity.enums.UserRole;
import com.example.bank.security.CurrentUser;
import com.example.bank.service.CardService;
import com.example.bank.service.ChatRoomService;
import com.example.bank.service.MessageService;
import com.example.bank.service.UserService;
import com.example.bank.service.impl.SendMailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final ChatRoomService chatRoomService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final MessageService messageService;

    private final CardService cardService;

    private final SendMailService sendMailService;

    @Value("${picture.upload.directory}")
    private String uploadDirectory;

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
                           @RequestParam(value = "phone", required = false) String phone,
                           @RequestParam(value = "picture", required = false) MultipartFile multipartFile) throws IOException {
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
        String picName = "";
        if (multipartFile != null && !multipartFile.isEmpty()) {
            picName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
            File file = new File(uploadDirectory, picName);
            multipartFile.transferTo(file);
        }
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setPassword(password);
        user.setAge(age);
        user.setPhone(phone);
        user.setPicName(picName);
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
        return "redirect:/user/login";
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
    public String verify(@RequestParam(name = "token", required = false) String stringToken,
                         ModelMap modelMap) {
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
        if (user.isActive()) {
            return "redirect:/user/newPassword/" + user.getEmail();
        }
        user.setActive(true);
        user.setToken(0);
        userService.save(user);
        return "redirect:/user/verification?msg=Verification Successes You Can Login";
    }

    @GetMapping("/user/history")
    public String historyPage() {
        return "/user/history";
    }


    @PostMapping("/user/forgetPassword")
    public String forgetPassword(@RequestParam(name = "msg", required = false) String msg,
                                 @RequestParam(value = "email", required = false) String email,
                                 ModelMap modelMap) {
        if (email == null || email == "") {
            return "redirect:/user/forgetPassword?msg=Invalid Email";
        }
        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            return "redirect:/user/forgetPassword?msg=Invalid Email";
        }
        if (msg != null && !msg.isEmpty()) {
            modelMap.addAttribute("msg", msg);
        }
        Random random = new Random();
        int randomNumber = random.nextInt(899999) + 100000;
        user.get().setToken(randomNumber);
        userService.save(user.get());
        try {
            sendMailService.sendVerificationMail(user.get().getEmail(), "Welcome" + " " + user.get().getName(), user.get(),
                    "mail/forgetPasswordMail");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/user/verification";
    }

    @GetMapping("/user/forgetPassword")
    public String forgetPasswordPage() {
        return "user/forgetPassword";
    }

    @GetMapping("/user/newPassword/{email}")
    public String newPasswordPage(@PathVariable("email") String email, ModelMap modelMap) {
        modelMap.addAttribute("email", email);
        return "/user/newPassword";
    }


    @PostMapping("/user/updatePassword")
    public String updatePassword(@RequestParam(value = "password", required = false) String newPassword,
                                 @RequestParam(value = "email", required = false) String email) {
        Optional<User> user = userService.findByEmail(email);
        user.get().setPassword(passwordEncoder.encode(newPassword));
        userService.save(user.get());
        return "redirect:/user/login?msg=Password Updated";
    }
}