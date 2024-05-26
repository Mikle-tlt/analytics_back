package com.example.analytics_back.DTO.analytics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfitabilityDTO {
    private Long productId;
    private String productName;
    private String categoryName;
    private int firstPeriod;
    private int secondPeriod;

    public ProfitabilityDTO(Long productId, String productName, String categoryName,
                            int firstPeriod, int secondPeriod) {
        this.productId = productId;
        this.productName = productName;
        this.categoryName = categoryName;
        this.firstPeriod = firstPeriod;
        this.secondPeriod = secondPeriod;
    }
}
