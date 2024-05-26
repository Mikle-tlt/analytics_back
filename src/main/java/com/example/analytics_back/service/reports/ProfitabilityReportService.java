package com.example.analytics_back.service.reports;

import com.example.analytics_back.DTO.analytics.ProfitabilityDTO;
import com.example.analytics_back.DTO.reports.ReportProfitabilityDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.ReportService;
import com.example.analytics_back.service.offlineAnalytics.OfflineProfitabilityService;
import com.example.analytics_back.service.onlineAnalytics.OnlineProfitabilityService;
import com.example.analytics_back.service.totalAnalytics.TotalProfitabilityService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfitabilityReportService {

    private final OfflineProfitabilityService offlineProfitabilityService;
    private final OnlineProfitabilityService onlineProfitabilityService;
    private final TotalProfitabilityService totalProfitabilityService;

    public void printProfitability (Document document, List<ReportProfitabilityDTO> reportProfitabilityDTOS, String variant)
            throws DocumentException, CustomException, ParseException, IOException {
        for (ReportProfitabilityDTO profitabilityDTO: reportProfitabilityDTOS) {
            ReportService.printBoldTextCenter(getNameOfReport(variant), document);

            String startDateFirst = profitabilityDTO.getStartPeriodFirst();
            String endDateFirst = profitabilityDTO.getEndPeriodFirst();

            String startDateSecond = profitabilityDTO.getStartPeriodSecond();
            String endDateSecond = profitabilityDTO.getEndPeriodSecond();

            ReportService.printDefaultText("Первый период: с " + startDateFirst + " по " + endDateFirst, document, true);
            ReportService.printDefaultText("Второй период: с " + startDateSecond + " по " + endDateSecond, document, true);

            Map<String, Object> result = getData(variant, startDateFirst, endDateFirst, startDateSecond, endDateSecond);

            List<ProfitabilityDTO> profitabilityDTOS = (List<ProfitabilityDTO>) result.get("result");
            String recommendation = result.get("label").toString();
            document.add(Chunk.NEWLINE);
            document.add(generatePdfTable(profitabilityDTOS));
            ReportService.printDefaultText(recommendation, document, false);
        }
    }

    public PdfPTable generatePdfTable(List<ProfitabilityDTO> profitabilityDTOS) {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        addTableHeader(table);
        addRows(table, profitabilityDTOS);
        return table;
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Название", "Первый период", "Второй период")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle,  ReportService.getFont(Font.NORMAL)));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, List<ProfitabilityDTO> profitabilityDTOS) {
        for (ProfitabilityDTO profitabilityDTO: profitabilityDTOS) {
            table.addCell(new PdfPCell(new Phrase(profitabilityDTO.getProductName() +
                    " (" + profitabilityDTO.getCategoryName() + ") ", ReportService.getFont(Font.NORMAL))));
            table.addCell(new PdfPCell(new Phrase("~" + profitabilityDTO.getFirstPeriod() + " %")));
            table.addCell(new PdfPCell(new Phrase("~" + profitabilityDTO.getSecondPeriod() + " %")));
        }
    }
    private Map<String, Object> getData(String variant, String startDateFirst, String endDateFirst,
                                        String startDateSecond, String endDateSecond)
            throws CustomException, ParseException, DocumentException, IOException {
        return switch (variant) {
            case "online" -> onlineProfitabilityService.profitabilityData(startDateFirst, endDateFirst, startDateSecond, endDateSecond);
            case "offline" -> offlineProfitabilityService.profitabilityData(startDateFirst, endDateFirst, startDateSecond, endDateSecond);
            case "total" -> totalProfitabilityService.profitabilityData(startDateFirst, endDateFirst, startDateSecond, endDateSecond);
            default -> null;
        };
    }

    private String getNameOfReport(String variant) {
        return switch (variant) {
            case "online" -> "Анализ рентабильности по онлайн данным";
            case "offline" -> "Анализ рентабильности по оффлайн данным";
            case "total" -> "Анализ рентабильности по онлайн и оффлан данным";
            default -> null;
        };
    }
}
