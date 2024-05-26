package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.OfflinePointProductsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.service.OfflinePointProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/offline/points/products")
@PreAuthorize("hasRole('MANAGER')")
public class OfflinePointProductsController {

    @Autowired
    private OfflinePointProductsService offlinePointProductsService;

    @GetMapping("/{offlinePointId}")
    public ResponseEntity<?> getOfflinePointProducts(@PathVariable Long offlinePointId) {
        try {
            List<OfflinePointProductsDTO> offlinePointProductsDTOList = offlinePointProductsService
                    .getOfflinePointProducts(offlinePointId);
            return ResponseEntity.ok(offlinePointProductsDTOList);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{offlinePointId}")
    public ResponseEntity<?> offlinePointProductsAdd(@RequestBody OfflinePointProductsDTO OfflinePointProductsDTO,
                                                     @PathVariable Long offlinePointId) {
        try {
            OfflinePointProductsDTO offlinePointProductsDTO = offlinePointProductsService
                    .offlinePointProductsAdd(OfflinePointProductsDTO, offlinePointId);
            return ResponseEntity.ok(offlinePointProductsDTO);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> offlinePointProductsEdit(@RequestBody OfflinePointProductsDTO OfflinePointProductsDTO) {
        try {
            OfflinePointProductsDTO updatedOfflinePointProduct = offlinePointProductsService
                    .offlinePointProductsEdit(OfflinePointProductsDTO);
            return ResponseEntity.ok(updatedOfflinePointProduct);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{offlinePointProductId}")
    public ResponseEntity<?> offlinePointProductsDelete(@PathVariable Long offlinePointProductId) {
        try {
            offlinePointProductsService.offlinePointProductsDelete(offlinePointProductId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
