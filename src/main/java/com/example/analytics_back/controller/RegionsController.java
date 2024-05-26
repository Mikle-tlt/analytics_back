package com.example.analytics_back.controller;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.Regions;
import com.example.analytics_back.service.RegionsService;
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
@RequestMapping("/regions")
@PreAuthorize("hasRole('MANAGER')")
public class RegionsController {
    @Autowired
    private RegionsService regionsService;

    @GetMapping
    public ResponseEntity<?> getRegions() {
        try {
            List<Regions> regionsList = regionsService.getRegions();
            return ResponseEntity.ok(regionsList);
        }  catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }  catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> regionAdd(@RequestBody Regions region) {
        try {
            Regions regions1 = regionsService.regionAdd(region.getName());
            return ResponseEntity.ok(regions1);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> regionEdit(@RequestBody Regions region) {
        try {
            Regions regions1 = regionsService.regionEdit(region);
            return ResponseEntity.ok(regions1);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{regionId}")
    public ResponseEntity<?> regionDelete(@PathVariable Long regionId) {
        try {
            regionsService.regionDelete(regionId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
