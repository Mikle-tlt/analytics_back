package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.DetailsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.Details;
import com.example.analytics_back.service.DTOConvectors.DetailsDTOConverter;
import com.example.analytics_back.service.DetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/online/buys/details")
@PreAuthorize("hasRole('MANAGER')")
public class DetailsController {
    @Autowired
    private DetailsService detailsService;

    @GetMapping("/{buyId}")
    public ResponseEntity<?> details(@PathVariable Long buyId) {
        try {
            List<DetailsDTO> buyDetails = detailsService.getBuyDetails(buyId);
            return ResponseEntity.ok(buyDetails);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{buyId}")
    public ResponseEntity<?> detailAdd(@PathVariable Long buyId, @RequestBody DetailsDTO detailsDTO) {
        try {
            DetailsDTO addedBuyDetail = detailsService.buyDetailAdd(buyId, detailsDTO);
            return ResponseEntity.ok(addedBuyDetail);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> detailEdit(@RequestBody DetailsDTO detailsDTO) {
        try {
            DetailsDTO detailsDTO1 = detailsService.detailEdit(detailsDTO);
            return ResponseEntity.ok(detailsDTO1);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{detailId}")
    public ResponseEntity<?> offlinePointProductsDelete(@PathVariable Long detailId) {
        try {
            detailsService.detailDelete(detailId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
