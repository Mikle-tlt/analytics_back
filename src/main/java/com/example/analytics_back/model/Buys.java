package com.example.analytics_back.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Buys {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private Date date;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Clients client;
    @OneToMany(mappedBy = "buy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Details> details;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Points points;


    public Buys(String date, Points points, Clients client) throws ParseException {
        this.date = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        this.points = points;
        this.client = client;
    }

    public void setDate(String date) throws ParseException {
        this.date = new SimpleDateFormat("yyyy-MM-dd").parse(date);
    }

    public String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public Date getOriginDate() {
        return date;
    }
    public String getToday() {return new SimpleDateFormat("yyyy-MM-dd").format(new Date());}

    public double getCostPrice() {
        if (details != null) {
            return details.stream()
                    .mapToDouble(detail -> detail.getQuantity() * detail.getProduct().getPrice())
                    .sum();
        } else {
            return 0;
        }
    }

    public double getRevenue() {
        if (details != null) {
            return details.stream()
                    .mapToDouble(detail -> detail.getQuantity() * detail.getPrice())
                    .sum();
        } else {
            return 0;
        }
    }

    public double getDifferent() {
        if (details != null) {
            return getRevenue() - getCostPrice();
        } else {
            return 0;
        }
    }
}