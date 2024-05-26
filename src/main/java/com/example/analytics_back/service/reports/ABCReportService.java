package com.example.analytics_back.service.reports;

import com.example.analytics_back.DTO.analytics.ABCDTO;
import com.example.analytics_back.DTO.reports.ReportABCDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.ReportService;
import com.example.analytics_back.service.offlineAnalytics.OfflineABCService;
import com.example.analytics_back.service.onlineAnalytics.OnlineABCService;
import com.example.analytics_back.service.totalAnalytics.TotalABCService;
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
public class ABCReportService {

    private final OnlineABCService onlineABCService;
    private final OfflineABCService offlineABCService;
    private final TotalABCService totalABCService;

    public void printABC (Document document, List<ReportABCDTO> reportABCDTO, String variant)
            throws DocumentException, CustomException, ParseException {
        for (ReportABCDTO abcdto: reportABCDTO) {
            ReportService.printBoldTextCenter(getNameOfReport(variant), document);

            String startDate = abcdto.getStartPeriod();
            String endDate = abcdto.getEndPeriod();

            ReportService.printDefaultText("С " + startDate +
                    " по " + endDate, document, true);

            Map<String, Object> result = getData(variant, startDate, endDate);

            List<ABCDTO> offlineABCDTOS = (List<ABCDTO>) result.get("result");
            String recommendation = result.get("label").toString();
            ReportService.printDefaultText("Общая выручка: " +
                    ReportService.calculateSum(offlineABCDTOS, ABCDTO::getRevenue)  +
                    "        Сумма себестоимостей: " +
                    ReportService.calculateSum(offlineABCDTOS, ABCDTO::getCostPrice)
                    + "        Общая прибыль: " +
                    ReportService.calculateSum(offlineABCDTOS, ABCDTO::getDifferent), document, true);
            document.add(Chunk.NEWLINE);
            document.add(generatePdfTable(offlineABCDTOS));
            ReportService.printDefaultText(recommendation, document, false);
        }
    }

    public PdfPTable generatePdfTable(List<ABCDTO> offlineABCDTOS) {
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        addTableHeader(table);
        addRows(table, offlineABCDTOS);
        return table;
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Название", "Количество", "Выручка", "Себестоимость", "Прибыль", "Доля прибыли", "Группа")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle, ReportService.getFont(Font.NORMAL)));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, List<ABCDTO> offlineABCDTOS) {
        for (ABCDTO abcdto: offlineABCDTOS) {
            table.addCell(new PdfPCell(new Phrase(abcdto.getProductName() +
                    " (" + abcdto.getCategoryName() + ") ", ReportService.getFont(Font.NORMAL))));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(abcdto.getQuantity()))));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(abcdto.getRevenue()))));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(abcdto.getCostPrice()))));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(abcdto.getDifferent()))));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(abcdto.getProfitShare()))));
            table.addCell(new PdfPCell(new Phrase(abcdto.getGroup())));
        }
    }
    private Map<String, Object> getData(String variant, String startDate, String endDate)
            throws CustomException, ParseException {
        return switch (variant) {
            case "online" -> onlineABCService.abcFiltered(startDate, endDate);
            case "offline" -> offlineABCService.abcFiltered(startDate, endDate);
            case "total" -> totalABCService.abcFiltered(startDate, endDate);
            default -> null;
        };
    }

    private String getNameOfReport(String variant) {
        return switch (variant) {
            case "online" -> "ABC-анализ по онлайн данным";
            case "offline" -> "ABC-анализ по оффлайн данным";
            case "total" -> "ABC-анализ по онлайн и оффлан данным";
            default -> null;
        };
    }
}
