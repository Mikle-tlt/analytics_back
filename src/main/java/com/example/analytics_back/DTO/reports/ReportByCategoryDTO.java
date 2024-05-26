package com.example.analytics_back.DTO.reports;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportByCategoryDTO {
    private Long categoryId;
    private int year;
}
