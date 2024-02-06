package com.example.analytics_back.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OfflineDetailsDTO {
    private Long id;
    private Long productId;
    private int quantity;
    private double price;

    public OfflineDetailsDTO(Long id, Long productId, int quantity, double price) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }
}
