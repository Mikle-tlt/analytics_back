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
public class Points {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    private String address;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Regions region;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Users owner;

    @OneToMany(mappedBy = "points", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Buys> buys;

    public Points(String address, Regions region, Users owner) {
        this.address = address;
        this.region = region;
        this.owner = owner;
    }
}