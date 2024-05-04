package com.example.bank.controller;

import com.example.bank.entity.ChatRoom;
import com.example.bank.entity.Message;
import com.example.bank.security.CurrentUser;
import com.example.bank.service.ChatRoomService;
import com.example.bank.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;

    @GetMapping("/admin/chats")
    public String contactAdminPage(@AuthenticationPrincipal CurrentUser springUser, ModelMap modelMap) {
        List<ChatRoom> chats = chatRoomService.getByAdmin(springUser.getUser());
        modelMap.addAttribute("chats", chats);
        return "admin/chats";
    }

    @GetMapping("/chat/{id}")
    public String chatPage(@AuthenticationPrincipal CurrentUser springUser,
                           @PathVariable("id") int id, ModelMap modelMap) {
        List<ChatRoom> chats = chatRoomService.getByAdmin(springUser.getUser());
        modelMap.addAttribute("chats", chats);
        ChatRoom chat = chatRoomService.getById(id);
        List<Message> messages = messageService.getByChatRoom(chat);
        modelMap.addAttribute("messages", messages);
        return "admin/chats";
    }

    @GetMapping("/admin/transactions")
    public String Transactions() {
        return "admin/transactions";
    }
}
