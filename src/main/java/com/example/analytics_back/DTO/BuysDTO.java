package com.example.analytics_back.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BuysDTO {
    private Long id;
    private String date;
    private Long pointId;
    private String pointAddress;
    private Double costPrice;
    private Double revenue;
    private Double different;

    public BuysDTO(Long id, String date, Long pointId) {
        this.id = id;
        this.date = date;
        this.pointId = pointId;
    }
    public BuysDTO(Long id, String date, Long pointId, String pointAddress,
                   Double costPrice, Double revenue, Double different) {
        this.id = id;
        this.date = date;
        this.pointId = pointId;
        this.pointAddress = pointAddress;
        this.costPrice = costPrice;
        this.revenue = revenue;
        this.different = different;
    }
}