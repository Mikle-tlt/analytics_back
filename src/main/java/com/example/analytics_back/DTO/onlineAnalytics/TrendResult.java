package com.example.analytics_back.DTO.onlineAnalytics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TrendResult {
    private double slope;
    private double intercept;

    public TrendResult(double slope, double intercept) {
        this.slope = slope;
        this.intercept = intercept;
    }
    public String getTrendDirection() {
        if (slope > 0) {
            return "был обнаружен рост в продажах";
        } else if (slope < 0) {
            return "было обнаружено падение в продажах";
        } else {
            return "было обнаружено незначительное отклонение от среднего числа продаж";
        }
    }
}