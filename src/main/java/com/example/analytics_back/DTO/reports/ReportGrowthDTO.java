package com.example.analytics_back.DTO.reports;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportGrowthDTO {
    private Long productId;
    private String startPeriod;
    private String endPeriod;
}
