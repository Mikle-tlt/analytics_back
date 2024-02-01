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
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String name;
    private double price;
   /* @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Details> details;*/
    /*@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OfflineProducts> offlineProducts;*/
    @ManyToOne(fetch = FetchType.LAZY)
    private Categories category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Users owner;

    public Products(Users owner, Categories category, String name, double price) {
        this.owner = owner;
        this.category = category;
        this.name = name;
        this.price = price;
    }
}
