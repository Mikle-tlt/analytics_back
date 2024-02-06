package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.DetailsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Details;
import com.example.analytics_back.service.DTOConvectors.DetailsDTOConverter;
import com.example.analytics_back.service.DetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/online/buys/details")
public class DetailsController {
    @Autowired
    private DetailsService detailsService;
    @Autowired
    private DetailsDTOConverter detailsDTOConverter;

    @GetMapping("/{buyId}")
    public ResponseEntity<?> details(@PathVariable Long buyId) {
        try {
            List<Details> details = detailsService.details(buyId);
            List<DetailsDTO> detailsDTOList = details.stream()
                    .map(detailsDTOConverter::convertToDTO)
                    .toList();
            return ResponseEntity.ok(detailsDTOList);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{buyId}")
    public ResponseEntity<?> detailAdd(@PathVariable Long buyId, @RequestBody DetailsDTO detailsDTO) {
        try {
            DetailsDTO detailsDTO1 = detailsService.detailAdd(buyId, detailsDTO);
            return ResponseEntity.ok(detailsDTO1);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> detailEdit(@RequestBody DetailsDTO detailsDTO) {
        try {
            DetailsDTO detailsDTO1 = detailsService.detailEdit(detailsDTO);
            return ResponseEntity.ok(detailsDTO1);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{detailId}")
    public ResponseEntity<?> offlinePointProductsDelete(@PathVariable Long detailId) {
        try {
            detailsService.detailDelete(detailId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
