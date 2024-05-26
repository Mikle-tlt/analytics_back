package com.example.analytics_back.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DetailsDTO {
    private Long id;
    private Long productId;
    private int quantity;
    private double price;
    private boolean oldProduct;
    private String category;

    public DetailsDTO(Long id, Long productId, int quantity, double price, String category) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
    }
    public DetailsDTO(Long productId, int quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }
    public DetailsDTO(Long id, Long productId, int quantity, double price, boolean oldProduct) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.oldProduct = oldProduct;
    }
}