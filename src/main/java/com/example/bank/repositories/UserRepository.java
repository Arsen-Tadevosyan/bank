package com.example.bank.repositories;

import com.example.bank.entity.User;
import com.example.bank.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

    List<User> findByUserRole(UserRole typeRole);
   Optional <User> findByEmail(String email);
}
