package com.example.analytics_back.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class OfflinePointProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private int quantity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Products product;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private OfflinePoints offlinePoints;
    @OneToMany(mappedBy = "offlinePointProducts", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OfflineDetails> offlineDetails;

    public OfflinePointProducts(int quantity, Products product, OfflinePoints offlinePoints) {
        this.quantity = quantity;
        this.product = product;
        this.offlinePoints = offlinePoints;
    }
    public int getQuantityReal() {
        return quantity;
    }
}