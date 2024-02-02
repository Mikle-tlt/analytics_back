package com.example.analytics_back.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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
   /* @OneToMany(mappedBy = "offline", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OfflineBuys> offlineBuys;
    @OneToMany(mappedBy = "offline", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OfflineProducts> offlineProducts;*/

    public OfflinePoints(String name, String address, Users owner, Regions region) {
        this.name = name;
        this.address = address;
        this.owner = owner;
        this.region = region;
    }

 /*   public void addOfflineBuy(OfflineBuys offlineBuy) {
        offlineBuys.add(offlineBuy);
        offlineBuy.setOffline(this);
    }

    public int getCost() {
        return offlineBuys.stream().reduce(0, (i, buy) -> i + buy.getCost(), Integer::sum);
    }

    public int getIncome() {
        return offlineBuys.stream().reduce(0, (i, buy) -> i + buy.getIncome(), Integer::sum);
    }

    public int getDifferent() {
        return getIncome() - getCost();
    }*/
}
