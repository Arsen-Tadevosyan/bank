package com.example.bank.controller;

import com.example.bank.entity.ChatRoom;
import com.example.bank.entity.Message;
import com.example.bank.entity.User;
import com.example.bank.entity.enums.UserRole;
import com.example.bank.security.CurrentUser;
import com.example.bank.service.ChatRoomService;
import com.example.bank.service.MessageService;
import com.example.bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;
    private final UserService userService;

    @PostMapping("/sendMessage")
    public String sendMessage(@AuthenticationPrincipal CurrentUser currentUser, @RequestParam(name = "message", required = false) String message, @RequestParam(name = "toEmail", required = false) String toEmail, @RequestParam(name = "fromEmail", required = false) String fromEmail) {
        if (message == null || message.isEmpty()) {
            if (currentUser.getUser().getUserRole().equals(UserRole.USER)) {
                return "redirect:/contactUs";
            } else {
                return "redirect:/admin/chats";
            }
        }
        ChatRoom chat = null;
        User user;
        if (currentUser.getUser().getUserRole().equals(UserRole.ADMIN)) {
            Optional<User> to = userService.findByEmail(toEmail);
            User toUser;
            if (to.get().equals(currentUser.getUser())) {
                Optional<User> from = userService.findByEmail(fromEmail);
                toUser = from.get();
            } else {
                toUser = to.get();
            }
            chat = chatRoomService.getByAdminAndUser(currentUser.getUser(), toUser);
            user = chat.getUser();
        } else {
            chat = chatRoomService.getByUser(currentUser.getUser());
            user = chat.getAdmin();
        }
        messageService.save(Message.builder().from(currentUser.getUser()).to(user).message(message).chatroom(chat).build());
        if (currentUser.getUser().getUserRole().equals(UserRole.USER)) {
            return "redirect:/contactUs";
        } else {
            return "redirect:/chat/" + chat.getId();
        }
    }
}
