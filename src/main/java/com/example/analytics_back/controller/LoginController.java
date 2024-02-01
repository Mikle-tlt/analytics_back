package com.example.analytics_back.controller;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import com.example.analytics_back.model.Users;

@RestController
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
public class LoginController {

    @Autowired
    private UsersService usersService;

    @PostMapping("/auth")
    public ResponseEntity<?> login(@RequestBody Users user){
        try {
            Users authenticatedUser = usersService.authenticate(user);
            return ResponseEntity.ok(authenticatedUser);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
