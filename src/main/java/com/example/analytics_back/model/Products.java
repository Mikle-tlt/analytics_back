package com.example.analytics_back.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OfflinePointProducts> offlinePointProductsList;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Details> details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
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

    public int getQuantity() {
        return details.stream().reduce(0, (i, detail) -> i + detail.getQuantity(), Integer::sum);
    }
    public int getQuantity(Date with, Date by) {
        if (details != null) {
            return details.stream().reduce(0, (i, detail) -> {
                if (with.before(detail.getBuy().getOriginDate()) && by.after(detail.getBuy().getOriginDate()))
                    return i + detail.getQuantity();
                return i;
            }, Integer::sum);
        } else {
            return 0;
        }
    }

    public int getQuantityOffline() {
        return offlinePointProductsList.stream().reduce(0, (i, product) -> i + product.getQuantity_(), Integer::sum);
    }

    public int getQuantityOffline(Date with, Date by) {
        return offlinePointProductsList.stream().reduce(0, (i, product) -> i + product.getQuantity(with, by), Integer::sum);
    }

    public double getCostPrice() {
        if (details != null) {
            return details.stream()
                    .mapToDouble(detail -> detail.getQuantity() * detail.getProduct().getPrice())
                    .sum();
        } else {
            return 0;
        }
    }
    public double getCostPrice(Date with, Date by) {
        if (details != null) {
            return details.stream().reduce(0.0, (i, detail) -> {
                if (with.before(detail.getBuy().getOriginDate()) && by.after(detail.getBuy().getOriginDate()))
                    return i + (detail.getQuantity() * detail.getProduct().getPrice());
                return i;
            }, Double::sum);
        } else {
            return 0;
        }
    }

    public double getCostPriceOffline() {
        return offlinePointProductsList.stream().reduce(0.0, (i, product) -> i + product.getCostPrice(), Double::sum);
    }

    public double getCostPriceOffline(Date with, Date by) {
        return offlinePointProductsList.stream().reduce(0.0, (i, product) -> i + product.getCostPrice(with, by), Double::sum);
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
    public double getRevenue(Date with, Date by) {
        if (details != null) {
            return details.stream().reduce(0.0, (i, detail) -> {
                if (with.before(detail.getBuy().getOriginDate()) && by.after(detail.getBuy().getOriginDate()))
                    return i + (detail.getQuantity() * detail.getPrice());
                return i;
            }, Double::sum);
        } else {
            return 0;
        }
    }

    public double getAverageRevenue(Date with, Date by, int numberDays) {
        if (details != null) {
            double res = details.stream().reduce(0.0, (i, detail) -> {
                if (with.before(detail.getBuy().getOriginDate()) && by.after(detail.getBuy().getOriginDate()))
                    return i + (detail.getQuantity() * detail.getPrice());
                return i;
            }, Double::sum);
            return res == 0 ? 0 : res / numberDays;
        } else {
            return 0;
        }
    }

    public double getRevenueOffline() {
        return offlinePointProductsList.stream().reduce(0.0, (i, product) -> i + product.getRevenue(), Double::sum);
    }

    public double getRevenueOffline(Date with, Date by) {
        return offlinePointProductsList.stream().reduce(0.0, (i, product) -> i + product.getRevenue(with, by), Double::sum);
    }

    public double getAverageRevenueOffline(Date with, Date by, int numberDays) {
        return offlinePointProductsList.stream().reduce(0.0, (i, product) -> i + product.getAverageRevenue(with, by, numberDays), Double::sum);
    }

    public double getDifferent() {
        return getRevenue() - getCostPrice();
    }
    public double getDifferent(Date with, Date by) {
        return getRevenue(with, by) - getCostPrice(with, by);
    }

    public double getDifferentOffline() { return getRevenueOffline() - getCostPriceOffline(); }

    public double getDifferentOffline(Date with, Date by) {
        return getRevenueOffline(with, by) - getCostPriceOffline(with, by);
    }

    public int standardDeviation(Date with, Date by, int numberDays) {
        double A = getAverageRevenue(with, by, numberDays);
        int one_day = 1000 * 60 * 60 * 24;
        int days = (int) (((by.getTime() - with.getTime()) / (one_day)) - 1);

        int[] array = new int[days];

        Date date = new Date();
        date.setTime(with.getTime());

        for (int i = 0; i < array.length; i++) {
            int res = 0;
            date.setTime(date.getTime() + one_day);

            for (Details d : details) {
                if (d.getBuy().getOriginDate().getTime() == date.getTime()) {
                    res += d.getQuantity() * d.getPrice();
                }
            }

            array[i] = res;
        }
        int sum = 0;
        for (int x : array) {
            sum += (int) Math.pow(x - A, 2);
        }
        return (int) Math.sqrt((double) sum / array.length);
    }

    public int standardDeviationOffline(Date with, Date by, int numberDays) {
        return offlinePointProductsList.stream().reduce(0, (i, product) -> i + product.standardDeviation(with, by, numberDays), Integer::sum);
    }
    public int rms(Date with, Date by, int numberDays) {
        try {
            return (int) (standardDeviation(with, by, numberDays) * 100 / getRevenue(with, by));
        } catch (Exception e) {
            return 0;
        }
    }

    public int rmsOffline(Date with, Date by, int numberDays) {
        try {
            return (int) (standardDeviationOffline(with, by, numberDays) * 100 / getRevenueOffline(with, by));
        } catch (Exception e) {
            return 0;
        }
    }
}
