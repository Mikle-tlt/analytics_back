package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.UserDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.service.ProfilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
@PreAuthorize("hasRole('ADMIN')")
public class ProfilesController {
    @Autowired
    private ProfilesService profilesService;

    @GetMapping
    public List<Users> getProfiles() {
        return profilesService.getProfiles();
    }

    @PutMapping
    public ResponseEntity<?> profileEdit(@RequestBody UserDTO user) {
        try {
            Users updatedUser = profilesService.profileEdit(user);
            return ResponseEntity.ok(updatedUser);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteProfile(@PathVariable Long userId) {
        try {
            profilesService.deleteProfile(userId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}