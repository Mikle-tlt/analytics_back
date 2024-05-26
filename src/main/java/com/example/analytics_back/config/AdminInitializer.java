package com.example.analytics_back.config;

import com.example.analytics_back.model.Users;
import com.example.analytics_back.model.enums.Role;
import com.example.analytics_back.repo.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Users user = new Users();
        if (usersRepository.findAll().isEmpty()) {
            user.setRole(Role.ADMIN);
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin"));
            usersRepository.save(user);
        }
    }
}