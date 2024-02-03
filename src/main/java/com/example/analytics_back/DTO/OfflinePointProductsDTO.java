package com.example.analytics_back.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OfflinePointProductsDTO {
    private Long id;
    private int quantity;
    private Long productId;

    public OfflinePointProductsDTO(Long id, int quantity, Long productId) {
        this.id = id;
        this.quantity = quantity;
        this.productId = productId;
    }
}
