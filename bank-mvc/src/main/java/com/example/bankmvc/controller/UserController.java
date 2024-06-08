package com.example.bankmvc.controller;

import com.example.bankcommon.dto.UserDto;
import com.example.bankcommon.dto.UserRegisterDto;
import com.example.bankcommon.entity.ChatRoom;
import com.example.bankcommon.entity.User;
import com.example.bankcommon.security.CurrentUser;
import com.example.bankcommon.service.CardService;
import com.example.bankcommon.service.ChatRoomService;
import com.example.bankcommon.service.UserService;
import com.example.bankcommon.service.impl.SendMailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final ChatRoomService chatRoomService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
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
    public String register(@Validated UserRegisterDto userDto,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) throws IOException {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addAttribute("msg", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/user/register";
        }
        if (!userService.findByEmail(userDto.getEmail()).isEmpty()) {
            redirectAttributes.addAttribute("msg", "Email already exists");
            return "redirect:/user/register";
        }
            String picName = "";
            MultipartFile multipartFile = userDto.getPicture();
            if (multipartFile != null && !multipartFile.isEmpty()) {
                picName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
                File file = new File(uploadDirectory, picName);
                multipartFile.transferTo(file);
            }
        User user = new User();
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setAge(userDto.getAge());
        user.setPhone(userDto.getPhone());
        user.setGender(userDto.getGender());
        user.setPicName(picName);
        try {
            userService.register(user, userDto.getMoneyType());
        } catch (Exception e) {
            redirectAttributes.addAttribute("msg", "Failed to register user");
            return "redirect:/user/register";
        }
        redirectAttributes.addAttribute("msg", "Registration successful. Please verify your email.");
        return "redirect:/user/verification";
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
            return "redirect:/";
        }
        return "redirect:/user/login";
    }

    @GetMapping("/user/profile")
    public String profilePage(@AuthenticationPrincipal CurrentUser currentUser, ModelMap modelMap) {
        modelMap.addAttribute("user", currentUser.getUser());
        modelMap.addAttribute("card", cardService.gatByUser(currentUser.getUser()));
        return "/user/profile";
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
        User randomAdmin = userService.findRandomAdmin();
        String chatId = chatRoomService.createChatId(user.getEmail(), randomAdmin.getEmail());
        log.info("ChatRoom already crated with {}", chatId);
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

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> findConnectedUsers(@AuthenticationPrincipal CurrentUser currentUser) {
        List<ChatRoom> byRecipientId = chatRoomService.findByRecipientId(currentUser.getUser().getEmail());
        List<UserDto> users = new ArrayList<>();
        for (ChatRoom chatRoom : byRecipientId) {
            String senderId = chatRoom.getSenderId();
            users.add(UserDto.builder()
                    .nickName(senderId)
                    .build());
        }
        return ResponseEntity.ok(users);
    }
}