package com.example.analytics_back.DTO.onlineAnalytics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OnlineABCDTO {
    private Long idProduct;
    private String productName;
    private String categoryName;
    private String group;
    private Double profitShare;
    private int quantity;
    private Double revenue;
    private Double costPrice;
    private Double different;

    public OnlineABCDTO(Long idProduct, String productName, String categoryName, String group,
                        Double profitShare, int quantity, Double costPrice, Double revenue,
                        Double different) {
        this.idProduct = idProduct;
        this.productName = productName;
        this.group = group;
        this.profitShare = profitShare;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.costPrice = costPrice;
        this.revenue = revenue;
        this.different = different;
    }
}
