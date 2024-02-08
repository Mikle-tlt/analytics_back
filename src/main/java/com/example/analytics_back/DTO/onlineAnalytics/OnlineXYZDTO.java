package com.example.analytics_back.DTO.onlineAnalytics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class OnlineXYZDTO {
    private Long idProduct;
    private String productName;
    private String categoryName;
    private Double revenue;
    private Double revenueAverage;
    private int standardDeviation;
    private int rms;
    private String group;

    public OnlineXYZDTO(Long idProduct, String productName, String categoryName,
                        Double revenue, Double revenueAverage, int standardDeviation, int rms, String group) {
        this.idProduct = idProduct;
        this.productName = productName;
        this.categoryName = categoryName;
        this.revenue = revenue;
        this.revenueAverage = revenueAverage;
        this.standardDeviation = standardDeviation;
        this.rms = rms;
        this.group = group;
    }
}
