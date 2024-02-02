package com.example.analytics_back.service.DTOConvectors;

import com.example.analytics_back.DTO.PointsDTO;
import com.example.analytics_back.model.Points;
import org.springframework.stereotype.Service;

@Service
public class PointsDTOConverter {
    public PointsDTO convertToDTO(Points point) {
        return new PointsDTO(
                point.getId(),
                point.getAddress(),
                point.getRegion() != null ? point.getRegion().getId() : null
        );
    }
}
