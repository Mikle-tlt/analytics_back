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
    /*@OneToMany(mappedBy = "offlineProduct", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OfflineDetails> offlineDetails;*/
    public OfflinePointProducts(int quantity, Products product, OfflinePoints offlinePoints) {
        this.quantity = quantity;
        this.product = product;
        this.offlinePoints = offlinePoints;
    }

    public int getQuantityReal() {
        return quantity;
    }

    /*public int getQuantity() {
        return offlineDetails.stream().reduce(0, (i, detail) -> i + detail.getQuantity(), Integer::sum);
    }

    public int getQuantity(Date with, Date by) {
        return offlineDetails.stream().reduce(0, (i, detail) -> {
            if (with.before(detail.getOfflineBuy().getOriginDate()) && by.after(detail.getOfflineBuy().getOriginDate()))
                return i + detail.getQuantity();
            return i;
        }, Integer::sum);
    }

    public int getCost() {
        return offlineDetails.stream().reduce(0, (i, detail) -> i + (detail.getQuantity() * product.getPrice()), Integer::sum);
    }

    public int getCost(Date with, Date by) {
        return offlineDetails.stream().reduce(0, (i, detail) -> {
            if (with.before(detail.getOfflineBuy().getOriginDate()) && by.after(detail.getOfflineBuy().getOriginDate()))
                return i + (detail.getQuantity() * product.getPrice());
            return i;
        }, Integer::sum);
    }

    public int getIncome() {
        return offlineDetails.stream().reduce(0, (i, detail) -> i + (detail.getQuantity() * detail.getPrice()), Integer::sum);
    }

    public int getIncome(Date with, Date by) {
        return offlineDetails.stream().reduce(0, (i, detail) -> {
            if (with.before(detail.getOfflineBuy().getOriginDate()) && by.after(detail.getOfflineBuy().getOriginDate()))
                return i + (detail.getQuantity() * detail.getPrice());
            return i;
        }, Integer::sum);
    }

    public int getIncome(Date with, Date by, int numberDays) {
        int res = offlineDetails.stream().reduce(0, (i, detail) -> {
            if (with.before(detail.getOfflineBuy().getOriginDate()) && by.after(detail.getOfflineBuy().getOriginDate()))
                return i + (detail.getQuantity() * detail.getPrice());
            return i;
        }, Integer::sum);
        return res == 0 ? 0 : res / numberDays;
    }

    public int standardDeviation(Date with, Date by, int numberDays) {
        int A = getIncome(with, by, numberDays);
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

    public int rms(Date with, Date by, int numberDays) {
        try {
            return standardDeviation(with, by, numberDays) * 100 / getIncome(with, by);
        } catch (Exception e) {
            return 0;
        }
    }

    public String rmsGroup(Date with, Date by, int numberDays) {
        int res = rms(with, by, numberDays);
        if (res < 10) return "X";
        else if (res >= 10 && res < 25) return "Y";
        else return "Z";
    }

    public int getDifferent() {
        return getIncome() - getCost();
    }

    public int getDifferent(Date with, Date by) {
        return getIncome(with, by) - getCost(with, by);
    }*/
}