package com.example.bankcommon.config;

import com.example.bankcommon.entity.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {


    private final PasswordEncoder passwordEncoder;


    private final UserDetailsService userDetailsService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/img/**").permitAll()
                        .requestMatchers("/aboutUs").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/user/register").permitAll()
                        .requestMatchers("/user/login").permitAll()
                        .requestMatchers("/user/verification").permitAll()
                        .requestMatchers("/user/forgetPassword").permitAll()
                        .requestMatchers("/user/newPassword/**").permitAll()
                        .requestMatchers("/user/updatePassword").permitAll()
                        .requestMatchers("/admin/**").hasAnyAuthority(UserRole.ADMIN.name())
                        .requestMatchers("/filterTransactions").hasAnyAuthority(UserRole.ADMIN.name())
                        .requestMatchers("/filterUsers").hasAnyAuthority(UserRole.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/user/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/loginSuccessUrl", true)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                );

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }
}
