package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.OfflinePointsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.OfflinePoints;
import com.example.analytics_back.service.DTOConvectors.OfflinePointsConverter;
import com.example.analytics_back.service.OfflinePointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/points/offline")
public class OfflinePointsController  {
    @Autowired
    private OfflinePointsService offlinePointsService;
    @Autowired
    private OfflinePointsConverter offlinePointsConverter;

    @GetMapping("/{userId}")
    public ResponseEntity<?> offlinePoints(@PathVariable Long userId) {
        try {
            List<OfflinePoints> offlinePointsList = offlinePointsService.offlinePoints(userId);
            List<OfflinePointsDTO> offlinePointsDTOList = offlinePointsList.stream()
                    .map(offlinePointsConverter::convertToDTO)
                    .toList();
            return ResponseEntity.ok(offlinePointsDTOList);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> offlinePointsAdd(@RequestBody OfflinePointsDTO offlinePointsDTO, @PathVariable Long userId) {
        try {
            OfflinePointsDTO offlinePoint = offlinePointsService.offlinePointsAdd(offlinePointsDTO, userId);
            return ResponseEntity.ok(offlinePoint);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> offlinePointsEdit(@RequestBody OfflinePointsDTO offlinePointsDTO) {
        try {
            OfflinePointsDTO offlinePoint = offlinePointsService.offlinePointsEdit(offlinePointsDTO);
            return ResponseEntity.ok(offlinePoint);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{offlinePointId}")
    public ResponseEntity<?> offlineDelete(@PathVariable Long offlinePointId) {
        try {
            offlinePointsService.offlinePointsDelete(offlinePointId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
