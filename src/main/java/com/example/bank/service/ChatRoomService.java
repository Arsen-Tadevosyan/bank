package com.example.bank.service;

import com.example.bank.entity.ChatRoom;
import com.example.bank.entity.User;

import java.util.List;

public interface ChatRoomService {

    ChatRoom save(ChatRoom chatRoom);

    ChatRoom getByUser(User user);

    ChatRoom getById(int id);

    List<ChatRoom> getByAdmin(User admin);

    ChatRoom getByAdminAndUser(User admin, User user);
}
