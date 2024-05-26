package com.example.analytics_back.DTO.reports;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportProfitabilityDTO {
    private String startPeriodFirst;
    private String endPeriodFirst;
    private String startPeriodSecond;
    private String endPeriodSecond;
}
