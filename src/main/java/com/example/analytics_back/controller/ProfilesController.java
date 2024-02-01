package com.example.analytics_back.controller;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.service.ProfilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class ProfilesController {
    @Autowired
    private ProfilesService profilesService;

    @GetMapping("")
    public List<Users> profiles() {
        return profilesService.profiles();
    }

    @PutMapping("/{userId}/edit/{role}")
    public ResponseEntity<?> profileEditRole(@PathVariable Long userId, @PathVariable String role) throws CustomException {
        try {
            Users user = profilesService.profileEditRole(userId, role);
            return ResponseEntity.ok(user);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteProfile(@PathVariable Long userId) {
        try {
            profilesService.deleteProfile(userId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody Users user) {
        try {
            Users user1 = profilesService.registration(user);
            return ResponseEntity.ok(user1);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}