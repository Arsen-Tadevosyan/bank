package com.example.bankcommon.repositories;

import com.example.bankcommon.entity.User;
import com.example.bankcommon.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    User findByToken(int token);

    User findRandomUserByUserRole(UserRole userRole);
}
