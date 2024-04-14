package com.example.bank.service.impl;

import com.example.bank.entity.ChatRoom;

import com.example.bank.entity.User;

import com.example.bank.entity.enums.UserRole;
import com.example.bank.repositories.ChatRoomRepository;
import com.example.bank.service.ChatRoomService;
import com.example.bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public ChatRoom getByUser(User user) {
        if (user.getUserRole().equals(UserRole.ADMIN)) {
            return chatRoomRepository.getByAdmin(user);
        }
        ChatRoom byUser = chatRoomRepository.findByUser(user);
        if (byUser != null) {
            return byUser;
        }
        List<User> admins = userService.findByUserRole(UserRole.ADMIN);
        if (admins.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(admins.size());
        User randomAdmin = admins.get(randomIndex);
        return  chatRoomRepository.save( ChatRoom.builder()
                .user(user)
                .admin(randomAdmin)
                .build());
    }
    @Override
    public ChatRoom getById(int id) {
        return chatRoomRepository.findById(id).orElse(null);
    }

    @Override
    public List<ChatRoom> getByAdmin(User admin) {
        return chatRoomRepository.findByAdmin(admin);
    }

    @Override
    public ChatRoom getByAdminAndUser(User admin, User user) {
        ChatRoom chat = chatRoomRepository.findByAdminAndUser(admin,user);
            return chat;
    }


}
