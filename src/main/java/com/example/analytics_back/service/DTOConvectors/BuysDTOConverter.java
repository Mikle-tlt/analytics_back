package com.example.analytics_back.service.DTOConvectors;

import com.example.analytics_back.DTO.BuysDTO;
import com.example.analytics_back.model.Buys;
import org.springframework.stereotype.Service;

@Service
public class BuysDTOConverter {
    public BuysDTO convertToDTO(Buys buys) {
        return new BuysDTO(
                buys.getId(),
                buys.getDate(),
                buys.getPoints() != null ? buys.getPoints().getId() : null
        );
    }
}