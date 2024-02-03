package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.OfflinePointProductsDTO;
import com.example.analytics_back.DTO.OfflinePointsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.OfflinePointProducts;
import com.example.analytics_back.model.OfflinePoints;
import com.example.analytics_back.repo.OfflinePointsRepository;
import com.example.analytics_back.service.DTOConvectors.OfflinePointProductsConvector;
import com.example.analytics_back.service.DTOConvectors.OfflinePointsConverter;
import com.example.analytics_back.service.OfflinePointProductsService;
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
@RequestMapping("/offline/points/products")
public class OfflinePointProductsController {

    @Autowired
    private OfflinePointProductsService offlinePointProductsService;
    @Autowired
    private OfflinePointProductsConvector offlinePointProductsConvector;

    @GetMapping("/{offlinePointId}")
    public ResponseEntity<?> offlinePointProducts(@PathVariable Long offlinePointId) {
        try {
            List<OfflinePointProducts> offlinePointProducts = offlinePointProductsService
                    .offlinePointProducts(offlinePointId);
            List<OfflinePointProductsDTO> offlinePointProductsDTOList = offlinePointProducts.stream()
                    .map(offlinePointProductsConvector::convertToDTO)
                    .toList();
            return ResponseEntity.ok(offlinePointProductsDTOList);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{offlinePointId}")
    public ResponseEntity<?> offlinePointProductsAdd(@RequestBody OfflinePointProductsDTO OfflinePointProductsDTO,
                                                     @PathVariable Long offlinePointId) {
        try {
            OfflinePointProductsDTO offlinePointProductsDTO = offlinePointProductsService
                    .offlinePointProductsAdd(OfflinePointProductsDTO, offlinePointId);
            return ResponseEntity.ok(offlinePointProductsDTO);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> offlinePointProductsEdit(@RequestBody OfflinePointProductsDTO OfflinePointProductsDTO) {
        try {
            OfflinePointProductsDTO offlinePointProductsDTO = offlinePointProductsService
                    .offlinePointProductsEdit(OfflinePointProductsDTO);
            return ResponseEntity.ok(offlinePointProductsDTO);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{offlinePointProductId}")
    public ResponseEntity<?> offlinePointProductsDelete(@PathVariable Long offlinePointProductId) {
        try {
            offlinePointProductsService.offlinePointProductsDelete(offlinePointProductId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
