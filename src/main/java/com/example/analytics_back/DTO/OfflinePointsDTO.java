package com.example.analytics_back.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OfflinePointsDTO {
    private Long id;
    private String address;
    private String name;
    private Long regionId;

    public OfflinePointsDTO(Long id, String address,String name, Long regionId) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.regionId = regionId;
    }
}
