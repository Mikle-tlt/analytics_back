package com.example.analytics_back.service;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.model.enums.Role;
import com.example.analytics_back.repo.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfilesService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Users> profiles() {
        List<Users> usersList = usersRepository.findAll();
        return usersList;
    }

    public Users profileEditRole(Long userId, String role) throws CustomException {
        Users user = usersRepository.getReferenceById(userId);
        if (user == null) {
            throw new CustomException("Редактируемый пользователь не найден в системе");
        }
        if(role == null || role.isEmpty()) {
            throw new CustomException("Значение роли не передано для измнения!");
        }
        user.setRole(Role.valueOf(role.toUpperCase()));
        Users newUser = usersRepository.save(user);
        return newUser;
    }

    public void deleteProfile(Long userId) throws CustomException {
        Users user = usersRepository.getReferenceById(userId);
        if (user == null) {
            throw new CustomException("Удаляемый пользователь не найден в системе");
        }
        usersRepository.deleteById(userId);
    }

    public Users registration(Users user) throws CustomException {
        if (user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty() ) {
            throw new CustomException("Некорретно введены данные для регистрации менеджера!");
        }
        if (usersRepository.findAll().isEmpty()) {
            user.setRole(Role.ADMIN);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return usersRepository.save(user);
        }
        if (usersRepository.findByUsername(user.getUsername()) != null) {
            throw new CustomException("Пользователь с данным именем уже существует в системе!");
        }
        user.setRole(Role.MANAGER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }
}
