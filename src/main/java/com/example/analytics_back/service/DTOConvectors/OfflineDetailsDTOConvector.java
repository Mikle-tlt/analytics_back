package com.example.analytics_back.service.DTOConvectors;

import com.example.analytics_back.DTO.OfflineDetailsDTO;
import com.example.analytics_back.model.OfflineDetails;
import org.springframework.stereotype.Service;

@Service
public class OfflineDetailsDTOConvector {
    public OfflineDetailsDTO convertToDTO(OfflineDetails offlineDetails) {
        return new OfflineDetailsDTO(
                offlineDetails.getId(),
                offlineDetails.getOfflinePointProducts() != null ?
                        offlineDetails.getOfflinePointProducts().getProduct().getId() : null,
                offlineDetails.getQuantity(),
                offlineDetails.getPrice()
        );
    }
}