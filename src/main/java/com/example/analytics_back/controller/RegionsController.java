package com.example.analytics_back.controller;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Regions;
import com.example.analytics_back.service.RegionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/regions")
public class RegionsController {
    @Autowired
    private RegionsService regionsService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> categories(@PathVariable Long userId) {
        try {
            List<Regions> regionsList = regionsService.regions(userId);
            return ResponseEntity.ok(regionsList);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> regionAdd(@RequestBody Regions regions, @PathVariable Long userId) {
        try {
            Regions regions1 = regionsService.regionAdd(regions.getName(), userId);
            return ResponseEntity.ok(regions1);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{regionId}")
    public ResponseEntity<?> regionEdit(@RequestBody Regions regions, @PathVariable Long regionId) {
        try {
            Regions regions1 = regionsService.regionEdit(regions.getName(), regionId);
            return ResponseEntity.ok(regions1);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{regionId}")
    public ResponseEntity<?> regionDelete(@PathVariable Long regionId) {
        try {
            regionsService.regionDelete(regionId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
