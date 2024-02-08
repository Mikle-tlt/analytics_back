package com.example.analytics_back.service.DTOConvectors;

import com.example.analytics_back.DTO.OfflinePointsDTO;
import com.example.analytics_back.model.OfflinePoints;
import org.springframework.stereotype.Service;

@Service
public class OfflinePointsConverter {
    public OfflinePointsDTO convertToDTO(OfflinePoints offlinePoints) {
        return new OfflinePointsDTO(
                offlinePoints.getId(),
                offlinePoints.getAddress(),
                offlinePoints.getName(),
                offlinePoints.getRegion() != null ? offlinePoints.getRegion().getId() : null
        );
    }
}