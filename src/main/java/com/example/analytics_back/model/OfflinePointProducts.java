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
    public int getQuantity_() {
        return offlineDetails.stream().reduce(0, (i, detail) -> i + detail.getQuantity(), Integer::sum);
    }
    public int getQuantity(Date with, Date by) {
        if (offlineDetails != null) {
            return offlineDetails.stream().reduce(0, (i, detail) -> {
                if (with.before(detail.getOfflineBuy().getOriginDate()) && by.after(detail.getOfflineBuy().getOriginDate()))
                    return i + detail.getQuantity();
                return i;
            }, Integer::sum);
        }
        return 0;
    }
    public double getCostPrice() {
        return offlineDetails.stream().reduce(0.0, (i, detail) -> i + (detail.getQuantity() * product.getPrice()),
                Double::sum);
    }
    public double getCostPrice(Date with, Date by) {
        if (offlineDetails != null) {
            return offlineDetails.stream().reduce(0.0, (i, detail) -> {
                if (with.before(detail.getOfflineBuy().getOriginDate()) && by.after(detail.getOfflineBuy().getOriginDate()))
                    return i + (detail.getQuantity() * product.getPrice());
                return i;
            }, Double::sum);
        }
        return 0;
    }

    public double getRevenue() {
        return offlineDetails.stream().reduce(0.0, (i, detail) -> i + (detail.getQuantity() * detail.getPrice()), Double::sum);
    }

    public double getRevenue(Date with, Date by) {
        if (offlineDetails != null) {
            return offlineDetails.stream().reduce(0.0, (i, detail) -> {
                if (with.before(detail.getOfflineBuy().getOriginDate()) && by.after(detail.getOfflineBuy().getOriginDate()))
                    return i + (detail.getQuantity() * detail.getPrice());
                return i;
            }, Double::sum);
        }
        return 0;
    }

    public double getAverageRevenue(Date with, Date by, int numberDays) {
        if (offlineDetails != null) {
            double res = offlineDetails.stream().reduce(0.0, (i, detail) -> {
                if (with.before(detail.getOfflineBuy().getOriginDate()) && by.after(detail.getOfflineBuy().getOriginDate()))
                    return i + (detail.getQuantity() * detail.getPrice());
                return i;
            }, Double::sum);
            return res == 0 ? 0 : res / numberDays;
        }
        return 0;
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

            for (OfflineDetails d : offlineDetails) {
                if (d.getOfflineBuy().getOriginDate().getTime() == date.getTime()) {
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
}