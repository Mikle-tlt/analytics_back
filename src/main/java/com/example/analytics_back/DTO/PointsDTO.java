package com.example.analytics_back.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PointsDTO {
    private Long id;
    private String address;
    private Long regionId;

    public PointsDTO(Long id, String address, Long regionId) {
        this.id = id;
        this.address = address;
        this.regionId = regionId;
    }
}
