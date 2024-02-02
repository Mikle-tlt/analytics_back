package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.ProductDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Products;
import com.example.analytics_back.service.DTOConvectors.ProductDTOConverter;
import com.example.analytics_back.service.ProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductsService productsService;
    @Autowired
    private ProductDTOConverter productDTOConverter;

    @GetMapping("/{userId}")
    public ResponseEntity<?> products(@PathVariable Long userId) {
        try {
            List<Products> productsList = productsService.products(userId);
            List<ProductDTO> productDTOList = productsList.stream()
                    .map(productDTOConverter::convertToDTO)
                    .toList();
            return ResponseEntity.ok(productDTOList);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @PostMapping("/{userId}")
    public ResponseEntity<?> productAdd(@RequestBody ProductDTO productDTO, @PathVariable Long userId) {
        try {
            ProductDTO product = productsService.productAdd(productDTO, userId);
            return ResponseEntity.ok(product);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> productEdit(@RequestBody ProductDTO productDTO) {
        try {
            ProductDTO product = productsService.productEdit(productDTO);
            return ResponseEntity.ok(product);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> productDelete(@PathVariable Long productId) {
        try {
            productsService.productDelete(productId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
