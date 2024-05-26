package com.example.analytics_back.controller;

import com.example.analytics_back.DTO.ProductDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.service.ProductsService;
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
@RequestMapping("/products")
@PreAuthorize("hasRole('MANAGER')")
public class ProductsController {

    @Autowired
    private ProductsService productsService;

    @GetMapping
    public ResponseEntity<?> getProducts() {
        try {
            List<ProductDTO> productDTOList = productsService.getProducts();
            return ResponseEntity.ok(productDTOList);
        } catch (CustomNotFoundException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PostMapping
    public ResponseEntity<?> productAdd(@RequestBody ProductDTO productDTO) {
        try {
            ProductDTO product = productsService.productAdd(productDTO);
            return ResponseEntity.ok(product);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> productEdit(@RequestBody ProductDTO productDTO) {
        try {
            ProductDTO product = productsService.productEdit(productDTO);
            return ResponseEntity.ok(product);
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> productDelete(@PathVariable Long productId) {
        try {
            productsService.productDelete(productId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (CustomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
