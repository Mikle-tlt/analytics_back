package com.example.analytics_back.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class OfflineDetails {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private OfflineBuys offlineBuy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private OfflinePointProducts offlinePointProducts;
    private int quantity;
    private double price;

    public OfflineDetails(OfflineBuys offlineBuy, OfflinePointProducts offlinePointProducts, int quantity, double price) {
        this.offlineBuy = offlineBuy;
        this.offlinePointProducts = offlinePointProducts;
        this.quantity = quantity;
        this.price = price;
    }
}