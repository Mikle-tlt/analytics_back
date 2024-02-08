package com.example.analytics_back.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class OfflineBuysDTO {

    private Long id;
    private String date;
    private Double costPrice;
    private Double revenue;
    private Double different;

    public OfflineBuysDTO(Long id, String date, Double costPrice, Double revenue, Double different) {
        this.id = id;
        this.date = date;
        this.costPrice = costPrice;
        this.revenue = revenue;
        this.different = different;
    }
}
