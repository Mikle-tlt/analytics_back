package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.OfflinePointsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.service.OfflinePointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/points/offline")
@PreAuthorize("hasRole('MANAGER')")
public class OfflinePointsController  {
    @Autowired
    private OfflinePointsService offlinePointsService;

    @GetMapping("/{offlinePointId}")
    public ResponseEntity<?> getOfflinePoint(@PathVariable Long offlinePointId) {
        try {
            OfflinePointsDTO offlinePointsDTO = offlinePointsService.getOfflinePointDTO(offlinePointId);
            return ResponseEntity.ok(offlinePointsDTO);
        } catch (UsernameNotFoundException | CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<?> getOfflinePoints() {
        try {
            List<OfflinePointsDTO> offlinePoints = offlinePointsService.getOfflinePoints();
            return ResponseEntity.ok(offlinePoints);
        } catch (UsernameNotFoundException | CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> offlinePointAdd(@RequestBody OfflinePointsDTO offlinePointsDTO) {
        try {
            OfflinePointsDTO offlinePoint = offlinePointsService.offlinePointAdd(offlinePointsDTO);
            return ResponseEntity.ok(offlinePoint);
        } catch (UsernameNotFoundException | CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> offlinePointEdit(@RequestBody OfflinePointsDTO offlinePointsDTO) {
        try {
            OfflinePointsDTO offlinePoint = offlinePointsService.offlinePointEdit(offlinePointsDTO);
            return ResponseEntity.ok(offlinePoint);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{offlinePointId}")
    public ResponseEntity<?> offlineDelete(@PathVariable Long offlinePointId) {
        try {
            offlinePointsService.offlinePointsDelete(offlinePointId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
