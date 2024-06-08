package com.example.bankrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@ComponentScan(basePackages ={"com.example.bankrest","com.example.bankcommon"} )
@SpringBootApplication
@EntityScan("com.example.bankcommon.entity")
@EnableJpaRepositories(basePackages = "com.example.bankcommon.repositories")
public class BankRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankRestApplication.class, args);
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
