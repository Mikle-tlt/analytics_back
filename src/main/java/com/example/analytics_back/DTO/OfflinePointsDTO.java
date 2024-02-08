package com.example.analytics_back.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OfflinePointsDTO {
    private Long id;
    private String address;
    private String name;
    private Long regionId;
    private String regionName;
    private Double costPrice;
    private Double revenue;
    private Double different;


    public OfflinePointsDTO(Long id, String address,String name, Long regionId) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.regionId = regionId;
    }
    public OfflinePointsDTO(Long id, String address, String name,
                            String regionName, Double costPrice, Double revenue, Double different) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.regionName = regionName;
        this.costPrice = costPrice;
        this.revenue = revenue;
        this.different = different;
    }
}
