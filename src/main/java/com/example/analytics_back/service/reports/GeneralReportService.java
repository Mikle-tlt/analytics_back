package com.example.analytics_back.service.reports;

import com.example.analytics_back.DTO.analytics.GeneralDTO;
import com.example.analytics_back.DTO.reports.ReportGeneralDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.ReportService;
import com.example.analytics_back.service.offlineAnalytics.OfflineGeneralService;
import com.example.analytics_back.service.onlineAnalytics.OnlineGeneralService;
import com.example.analytics_back.service.totalAnalytics.TotalGeneralService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeneralReportService {
    private final OfflineGeneralService offlineGeneralService;
    private final OnlineGeneralService onlineGeneralService;
    private final TotalGeneralService totalGeneralService;

    public void printGeneral (Document document, List<ReportGeneralDTO> reportGeneralDTO, String variant)
            throws DocumentException, IOException, CustomException, ParseException {
        for (ReportGeneralDTO generalDTO: reportGeneralDTO) {
            ReportService.printBoldTextCenter(getNameOfReport(variant), document);

            String startDate = generalDTO.getStartPeriod();
            String endDate = generalDTO.getEndPeriod();

            ReportService.printDefaultText("С " + startDate + " по " + endDate, document, true);
            document.add(Chunk.NEWLINE);

            List<GeneralDTO> result = getData(variant, startDate, endDate);
            document.add(generatePdfTable(result));
        }
    }

    public PdfPTable generatePdfTable(List<GeneralDTO> generalDTOS) throws DocumentException, IOException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        addTableHeader(table);
        addRows(table, generalDTOS);
        return table;
    }

    private void addTableHeader(PdfPTable table) throws DocumentException, IOException {
        BaseFont baseFont = BaseFont.createFont("c:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(baseFont, 10, Font.NORMAL);

        Stream.of("Название", "Количество", "Выручка", "Себестоимость", "Прибыль")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle, font));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, List<GeneralDTO> generalDTOS) {
        for (GeneralDTO generalDTO: generalDTOS) {
            table.addCell(new PdfPCell(new Phrase(generalDTO.getProductName() +
                    " (" + generalDTO.getCategoryName() + ") ", ReportService.getFont(Font.NORMAL))));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(generalDTO.getQuantity()))));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(generalDTO.getRevenue()))));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(generalDTO.getCostPrice()))));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(generalDTO.getDifferent()))));
        }

        table.addCell(new PdfPCell(new Phrase("Итого", ReportService.getFont(Font.NORMAL))));
        table.addCell(new PdfPCell(new Phrase(String.valueOf(ReportService.calculateSum(generalDTOS, GeneralDTO::getQuantity)))));
        table.addCell(new PdfPCell(new Phrase(String.valueOf(ReportService.calculateSum(generalDTOS, GeneralDTO::getRevenue)))));
        table.addCell(new PdfPCell(new Phrase(String.valueOf(ReportService.calculateSum(generalDTOS, GeneralDTO::getCostPrice)))));
        table.addCell(new PdfPCell(new Phrase(String.valueOf(ReportService.calculateSum(generalDTOS, GeneralDTO::getDifferent)))));
    }

    private List<GeneralDTO> getData(String variant, String startDate, String endDate)
            throws CustomException, ParseException {
        return switch (variant) {
            case "online" -> onlineGeneralService.generalFiltered(startDate, endDate);
            case "offline" -> offlineGeneralService.generalFiltered(startDate, endDate);
            case "total" -> totalGeneralService.generalFiltered(startDate, endDate);
            default -> null;
        };
    }

    private String getNameOfReport(String variant) {
        return switch (variant) {
            case "online" -> "Общая сводка по онлайн продажам";
            case "offline" -> "Общая сводка по оффлайн продажам";
            case "total" -> "Общая сводка по онлайн и оффлан продажам";
            default -> null;
        };
    }
}
