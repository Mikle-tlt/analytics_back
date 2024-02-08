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
public class OfflinePoints {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String name;
    private String address;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Users owner;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Regions region;
    @OneToMany(mappedBy = "offlinePoints", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OfflinePointProducts> offlinePointProducts;
    @OneToMany(mappedBy = "offlinePoints", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OfflineBuys> offlineBuys;

    public OfflinePoints(String name, String address, Users owner, Regions region) {
        this.name = name;
        this.address = address;
        this.owner = owner;
        this.region = region;
    }
    public double getCostPrice() {
        return offlineBuys.stream()
                .mapToDouble(OfflineBuys::getCostPrice)
                .sum();
    }
    public double getRevenue() {
        return offlineBuys.stream()
                .mapToDouble(OfflineBuys::getRevenue)
                .sum();
    }
    public double getDifferent() {
        return getRevenue() - getCostPrice();
    }
}
