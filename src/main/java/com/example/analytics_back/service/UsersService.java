package com.example.analytics_back.service;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.repo.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Users authenticate(Users userData) throws CustomException {
        if (userData.getUsername() == null || userData.getUsername().isEmpty() ||
                userData.getPassword() == null || userData.getPassword().isEmpty() ) {
            throw new CustomException("Некорретно введены данные для авторизации!");
        }
        Users user = usersRepository.findByUsername(userData.getUsername());
        if (user == null) {
            throw new CustomException("Пользователя с данным логином не существует в системе!");
        }
        String rawPassword = userData.getPassword();
        if (user != null && !passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new CustomException("Проверьте корректность введенного пароля и повторите попытку!");
        }
        return user;
    }
}
