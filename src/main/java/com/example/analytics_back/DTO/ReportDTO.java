package com.example.analytics_back.DTO;

import com.example.analytics_back.DTO.reports.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReportDTO {

    private List<ReportABCDTO> offlineABC;
    private boolean offlineAssortment;
    private List<ReportGeneralDTO> offlineGeneral;
    private List<ReportGrowthDTO> offlineGrowth;
    private List<ReportProfitabilityDTO> offlineProfitability;
    private boolean offlineRegion;
    private List<ReportByCategoryDTO> offlineByCategory;
    private List<ReportXYZDTO> offlineXYZ;

    private List<ReportABCDTO> onlineABC;
    private List<Integer> onlineCustomers;
    private List<ReportGeneralDTO> onlineGeneral;
    private List<ReportGrowthDTO> onlineGrowth;
    private List<ReportProfitabilityDTO> onlineProfitability;
    private boolean onlineRegion;
    private List<ReportByCategoryDTO> onlineByCategory;
    private List<ReportXYZDTO> onlineXYZ;

    private List<ReportABCDTO> totalABC;
    private List<ReportGeneralDTO> totalGeneral;
    private List<ReportGrowthDTO> totalGrowth;
    private List<ReportProfitabilityDTO> totalProfitability;
    private boolean totalRegion;
    private List<ReportByCategoryDTO> totalByCategory;
    private List<ReportXYZDTO> totalXYZ;
}
