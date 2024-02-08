package com.example.analytics_back.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class OfflineBuys {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private Date date;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private OfflinePoints offlinePoints;
    @OneToMany(mappedBy = "offlineBuy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OfflineDetails> offlineDetails;

    public OfflineBuys(String date, OfflinePoints offlinePoint) throws ParseException {
        this.date = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        this.offlinePoints = offlinePoint;
    }

    public void setDate(String date) throws ParseException {
        this.date = new SimpleDateFormat("yyyy-MM-dd").parse(date);
    }

    public String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
    public String getToday() {return new SimpleDateFormat("yyyy-MM-dd").format(new Date());}

    public Date getOriginDate() {
        return date;
    }

    public double getCostPrice() {
        if (offlineDetails != null) {
            return offlineDetails.stream()
                    .mapToDouble(detail -> detail.getQuantity() * detail.getOfflinePointProducts().getProduct().getPrice())
                    .sum();
        } else {
            return 0;
        }
    }

    public double getRevenue() {
        if (offlineDetails != null) {
            return offlineDetails.stream()
                    .mapToDouble(detail -> detail.getQuantity() * detail.getPrice())
                    .sum();
        } else {
            return 0;
        }
    }

    public double getDifferent() {
        if (offlineDetails != null) {
            return getRevenue() - getCostPrice();
        } else {
            return 0;
        }
    }
}