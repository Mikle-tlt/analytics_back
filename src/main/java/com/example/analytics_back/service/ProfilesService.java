package com.example.analytics_back.service;

import com.example.analytics_back.DTO.UserDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.model.enums.Role;
import com.example.analytics_back.repo.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfilesService {
    private final UsersRepository usersRepository;
    private final UsersService usersService;

    public List<Users> getProfiles() {
        return usersRepository.findAll();
    }
    public Users profileEdit(UserDTO user) throws CustomException {
        Users updatedUser = usersService.getUserById(user.getId());
        if(user.getRole() == null || user.getRole().isEmpty()) {
            throw new CustomException("Значение роли не передано для измнения!");
        }
        updatedUser.setRole(Role.valueOf(user.getRole().toUpperCase()));
        return usersRepository.save(updatedUser);
    }

    public void deleteProfile(Long userId) {
        Users user = usersService.getUserById(userId);
        usersRepository.delete(user);
    }
}
