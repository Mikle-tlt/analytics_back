package com.example.analytics_back.controller;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.example.analytics_back.model.Users;

@RestController
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private UsersService usersService;

    @PostMapping("/auth")
    public ResponseEntity<?> login(@RequestBody Users user){
        try {
            String authenticatedUser = usersService.authenticate(user);
            return ResponseEntity.ok(authenticatedUser);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody Users user) {
        try {
            Users addedUser = usersService.registration(user);
            return ResponseEntity.ok(addedUser);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
