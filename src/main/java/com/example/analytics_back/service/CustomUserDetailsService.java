package com.example.analytics_back.service;

import com.example.analytics_back.model.Users;
import com.example.analytics_back.repo.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователя с именем " + username +
                        " нет в системе"));
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().toString())
                .build();
    }
    public Users loadUserInfoByUsername(String username) throws UsernameNotFoundException {
        return usersRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователя с именем " + username +
                        " нет в системе"));
    }
}