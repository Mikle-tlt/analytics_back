package com.example.analytics_back.service;

import com.example.analytics_back.config.JwtTokenProvider;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.model.enums.Role;
import com.example.analytics_back.repo.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsersService {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;

    public String authenticate(Users userData) throws CustomException {
        String username = userData.getUsername();
        String password = userData.getPassword();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new CustomException("Некорретные имя пользователя или пароль!");
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,
                null, userDetails.getAuthorities());
        return jwtTokenProvider.generateToken(authentication);
    }
    public Users registration(Users user) throws CustomException {
        if (user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty() ) {
            throw new CustomException("Некорретно введены данные для регистрации!");
        }
        if (usersRepository.findAll().isEmpty()) {
            user.setRole(Role.ADMIN);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return usersRepository.save(user);
        }
        if (usersRepository.existsByUsername(user.getUsername())) {
            throw new CustomException("Пользователя с именем " + user.getUsername() +
                    " уже существует в системе");
        }
        user.setRole(Role.MANAGER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }
    public Users getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return customUserDetailsService.loadUserInfoByUsername(username);
    }
    public Users getUserById(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new CustomNotFoundException("Невозможно получить данные пользователя!"));
    }
}
