package com.example.analytics_back.service.reports;

import com.example.analytics_back.DTO.analytics.XYZDTO;
import com.example.analytics_back.DTO.reports.ReportXYZDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.ReportService;
import com.example.analytics_back.service.offlineAnalytics.OfflineXYZService;
import com.example.analytics_back.service.onlineAnalytics.OnlineXYZService;
import com.example.analytics_back.service.totalAnalytics.TotalXYZService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class XYZReportService {

    private final OfflineXYZService offlineXYZService;
    private final OnlineXYZService onlineXYZService;
    private final TotalXYZService totalXYZService;

    public void printXYZ (Document document, List<ReportXYZDTO> reportXYZDTOS, String variant)
            throws DocumentException, CustomException, ParseException {
        for (ReportXYZDTO xyzdto: reportXYZDTOS) {
            ReportService.printBoldTextCenter(getNameOfReport(variant), document);

            String startDate = xyzdto.getStartPeriod();
            String endDate = xyzdto.getEndPeriod();

            ReportService.printDefaultText("С " + startDate +
                    " по " + endDate, document, true);

            Map<String, Object> result = getData(variant, startDate, endDate);

            List<XYZDTO> xyzdtos = (List<XYZDTO>) result.get("result");
            String recommendation = result.get("label").toString();

            document.add(Chunk.NEWLINE);
            document.add(generatePdfTable(xyzdtos));
            ReportService.printDefaultText(recommendation, document, false);
        }
    }

    public PdfPTable generatePdfTable(List<XYZDTO> xyzdtos) {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        addTableHeader(table);
        addRows(table, xyzdtos);
        return table;
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Название", "Выручка", "Средняя выручка", "Средневзвешенное отклонение",
                        "Среднеквадратическое отклонение", "Группа")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle, ReportService.getFont(Font.NORMAL)));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, List<XYZDTO> xyzdtos) {
        for (XYZDTO xyzdto: xyzdtos) {
            table.addCell(new PdfPCell(new Phrase(xyzdto.getProductName() +
                    " (" + xyzdto.getCategoryName() + ") ", ReportService.getFont(Font.NORMAL))));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(xyzdto.getRevenue()))));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(xyzdto.getRevenueAverage()))));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(xyzdto.getStandardDeviation()))));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(xyzdto.getRms()))));
            table.addCell(new PdfPCell(new Phrase(xyzdto.getGroup())));
        }
    }

    private Map<String, Object> getData(String variant, String startDate, String endDate)
            throws CustomException, ParseException {
        return switch (variant) {
            case "online" -> onlineXYZService.xyzFiltered(startDate, endDate);
            case "offline" -> offlineXYZService.xyzFiltered(startDate, endDate);
            case "total" -> totalXYZService.xyzFiltered(startDate, endDate);
            default -> null;
        };
    }

    private String getNameOfReport(String variant) {
        return switch (variant) {
            case "online" -> "XYZ-анализ по онлайн продажам";
            case "offline" -> "XYZ-анализ по оффлайн продажам";
            case "total" -> "XYZ-анализ по онлайн и оффлан продажам";
            default -> null;
        };
    }
}
