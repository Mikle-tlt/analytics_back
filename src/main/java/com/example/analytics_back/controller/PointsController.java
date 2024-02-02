package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.PointsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Points;
import com.example.analytics_back.service.DTOConvectors.PointsDTOConverter;
import com.example.analytics_back.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/points")
public class PointsController {
    @Autowired
    private PointsService pointsService;
    @Autowired
    private PointsDTOConverter pointsDTOConverter;

    @GetMapping("/{userId}")
    public ResponseEntity<?> points(@PathVariable Long userId) {
        try {
            List<Points> pointsList = pointsService.points(userId);
            List<PointsDTO> pointsDTOList = pointsList.stream()
                    .map(pointsDTOConverter::convertToDTO)
                    .toList();
            return ResponseEntity.ok(pointsDTOList);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> pointAdd(@RequestBody PointsDTO pointsDTO, @PathVariable Long userId) {
        try {
            PointsDTO point = pointsService.pointAdd(pointsDTO, userId);
            return ResponseEntity.ok(point);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> pointEdit(@RequestBody PointsDTO pointsDTO) {
        try {
            PointsDTO point = pointsService.pointEdit(pointsDTO);
            return ResponseEntity.ok(point);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{pointId}")
    public ResponseEntity<?> productDelete(@PathVariable Long pointId) {
        try {
            pointsService.pointDelete(pointId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
