package com.example.analytics_back.service.DTOConvectors;

import com.example.analytics_back.DTO.DetailsDTO;
import com.example.analytics_back.model.Details;
import org.springframework.stereotype.Service;

@Service
public class DetailsDTOConverter {
    public DetailsDTO convertToDTO(Details details) {
        return new DetailsDTO(
                details.getId(),
                details.getProduct() != null ? details.getProduct().getId() : null,
                details.getQuantity(),
                details.getPrice(),
                details.getProduct() != null ? details.getProduct().getCategory().getName() : null
        );
    }
}