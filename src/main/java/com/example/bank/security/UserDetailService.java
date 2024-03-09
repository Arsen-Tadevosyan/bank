package com.example.bank.security;



import com.example.bank.entity.User;
import com.example.bank.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> byEmail = userRepository.findByEmail(username);

        if (byEmail.isEmpty()) {
            throw new UsernameNotFoundException("user with " + username + " dose not exists");
        }

        return new SpringUser(byEmail.get());
    }
}
