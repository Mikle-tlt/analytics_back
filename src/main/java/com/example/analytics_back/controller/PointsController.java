package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.PointsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/points")
@PreAuthorize("hasRole('MANAGER')")
public class PointsController {
    @Autowired
    private PointsService pointsService;

    @GetMapping
    public ResponseEntity<?> getPoints() {
        try {
            List<PointsDTO> pointsDTOList = pointsService.getPoints();
            return ResponseEntity.ok(pointsDTOList);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> pointAdd(@RequestBody PointsDTO pointsDTO) {
        try {
            PointsDTO point = pointsService.pointAdd(pointsDTO);
            return ResponseEntity.ok(point);
        } catch (CustomNotFoundException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> pointEdit(@RequestBody PointsDTO pointsDTO) {
        try {
            PointsDTO point = pointsService.pointEdit(pointsDTO);
            return ResponseEntity.ok(point);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{pointId}")
    public ResponseEntity<?> productDelete(@PathVariable Long pointId) {
        try {
            pointsService.pointDelete(pointId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
