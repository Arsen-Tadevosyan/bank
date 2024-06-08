package com.example.bankcommon.config;

import com.example.bankcommon.entity.User;
import com.example.bankcommon.entity.enums.UserRole;
import com.example.bankcommon.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Checking for admin user...");
        if (!userRepository.findByEmail("admin@gmail.com").isPresent()) {
            log.info("Admin user not found, creating new admin user...");
            User adminUser = User.builder()
                    .name("Admin")
                    .surname("User")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("123123"))
                    .userRole(UserRole.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(adminUser);
            log.info("Admin user created with email: admin@gmail.ru");
        } else {
            log.info("Admin user already exists.");
        }
    }
}