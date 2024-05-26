package com.example.analytics_back.DTO.analytics;

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
        String label = ". Это отслеживается с помощью аппроксимации графика";
        if (slope > 0) {
            return "был обнаружен рост в продажах" + label;
        } else if (slope < 0) {
            return "было обнаружено падение в продажах" + label;
        } else if (slope == 0) {
            return "продаж не обнаружено.";
        } else {
            return "было обнаружено незначительное отклонение от среднего числа продаж" + label;
        }
    }
}