package com.example.analytics_back.service.DTOConvectors;

import com.example.analytics_back.DTO.ProductDTO;
import com.example.analytics_back.model.Products;
import org.springframework.stereotype.Service;

@Service
public class ProductDTOConverter {
    public ProductDTO convertToDTO(Products product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory() != null ? product.getCategory().getId() : null
        );
    }
}
