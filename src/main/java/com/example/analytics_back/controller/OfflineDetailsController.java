package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.OfflineDetailsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.OfflineDetails;
import com.example.analytics_back.service.DTOConvectors.OfflineDetailsDTOConvector;
import com.example.analytics_back.service.OfflineDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/offline/points/buys/details")
public class OfflineDetailsController {
    @Autowired
    private OfflineDetailsService offlineDetailsService;
    @Autowired
    private OfflineDetailsDTOConvector offlineDetailsDTOConvector;

    @GetMapping("/{offlineBuyId}")
    public ResponseEntity<?> offlineDetails(@PathVariable Long offlineBuyId) {
        try {
            List<OfflineDetails> offlineDetails = offlineDetailsService
                    .offlineDetails(offlineBuyId);
            List<OfflineDetailsDTO> offlineDetailsDTOList = offlineDetails.stream()
                    .map(offlineDetailsDTOConvector::convertToDTO)
                    .toList();
            return ResponseEntity.ok(offlineDetailsDTOList);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{offlineBuyId}")
    public ResponseEntity<?> offlineDetailAdd(@PathVariable Long offlineBuyId, @RequestBody OfflineDetailsDTO offlineDetailsDTO) {
        try {
            OfflineDetailsDTO offlineDetail = offlineDetailsService.offlineDetailAdd(offlineBuyId, offlineDetailsDTO);
            return ResponseEntity.ok(offlineDetail);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{offlineDetailId}")
    public ResponseEntity<?> offlinePointProductsDelete(@PathVariable Long offlineDetailId) {
        try {
            offlineDetailsService.offlineDetailsDelete(offlineDetailId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
