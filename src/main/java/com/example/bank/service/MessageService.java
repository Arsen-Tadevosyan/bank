package com.example.bank.service;

import com.example.bank.entity.ChatRoom;
import com.example.bank.entity.Message;

import java.util.List;

public interface MessageService {

    List<Message> getByChatRoom(ChatRoom chatRoom);

    Message save(Message message);
}
