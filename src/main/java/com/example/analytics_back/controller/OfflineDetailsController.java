package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.OfflineDetailsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.service.OfflineDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/offline/points/buys/details")
@PreAuthorize("hasRole('MANAGER')")
public class OfflineDetailsController {
    @Autowired
    private OfflineDetailsService offlineDetailsService;

    @GetMapping("/{offlineBuyId}")
    public ResponseEntity<?> offlineDetails(@PathVariable Long offlineBuyId) {
        try {
            List<OfflineDetailsDTO> offlineDetailsDTOList = offlineDetailsService
                    .getOfflineDetails(offlineBuyId);
            return ResponseEntity.ok(offlineDetailsDTOList);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{offlineBuyId}")
    public ResponseEntity<?> offlineDetailAdd(@PathVariable Long offlineBuyId,
                                              @RequestBody OfflineDetailsDTO offlineDetailsDTO) {
        try {
            OfflineDetailsDTO offlineDetail = offlineDetailsService
                    .offlineDetailAdd(offlineBuyId, offlineDetailsDTO);
            return ResponseEntity.ok(offlineDetail);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{offlineDetailId}")
    public ResponseEntity<?> offlineDetailDelete(@PathVariable Long offlineDetailId) {
        try {
            offlineDetailsService.offlineDetailDelete(offlineDetailId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
