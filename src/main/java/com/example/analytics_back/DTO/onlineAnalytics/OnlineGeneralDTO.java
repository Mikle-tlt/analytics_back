package com.example.analytics_back.DTO.onlineAnalytics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class OnlineGeneralDTO {
    private Long idProduct;
    private String productName;
    private String categoryName;
    private int quantity;
    private Double revenue;
    private Double costPrice;
    private Double different;

    public OnlineGeneralDTO(Long idProduct, String productName, String categoryName, int quantity,
                            Double costPrice, Double revenue, Double different) {
        this.idProduct = idProduct;
        this.productName = productName;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.costPrice = costPrice;
        this.revenue = revenue;
        this.different = different;
    }
}