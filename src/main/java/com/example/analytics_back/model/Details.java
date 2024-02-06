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
public class Details {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Products product;
    private int quantity;
    private double price;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Buys buy;

    public Details(Buys buy, Products product, int quantity, double price) {
        this.buy = buy;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }
}