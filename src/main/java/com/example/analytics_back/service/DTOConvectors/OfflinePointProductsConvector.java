package com.example.analytics_back.service.DTOConvectors;

import com.example.analytics_back.DTO.OfflinePointProductsDTO;
import com.example.analytics_back.model.OfflinePointProducts;
import org.springframework.stereotype.Service;

@Service
public class OfflinePointProductsConvector {
    public OfflinePointProductsDTO convertToDTO(OfflinePointProducts offlinePointProduct) {
        return new OfflinePointProductsDTO(
                offlinePointProduct.getId(),
                offlinePointProduct.getQuantity(),
                offlinePointProduct.getProduct() != null ? offlinePointProduct.getProduct().getId() : null
        );
    }
}
