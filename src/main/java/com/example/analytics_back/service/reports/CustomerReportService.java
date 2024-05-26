package com.example.analytics_back.service.reports;

import com.example.analytics_back.service.ReportService;
import com.example.analytics_back.service.onlineAnalytics.OnlineCustomersService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerReportService {

    private final OnlineCustomersService onlineCustomersService;

    public void printCustomer (Document document, List<Integer> years) throws DocumentException {
        for (int year: years) {
            ReportService.printBoldTextCenter("Анализ клиентской базы по онлайн продажам за " + year + " год",
                    document);

            Map<String, Object> result = onlineCustomersService.getCustomersAnalytics(year);

            String[] arrayOfMonths = (String[]) result.get("month");
            int[][] customers = (int[][]) result.get("customers");
            String recommendation = result.get("labelText").toString();

            document.add(Chunk.NEWLINE);
            document.add(generatePdfTable(customers, arrayOfMonths));
            ReportService.printDefaultText(recommendation, document, false);
        }
    }

    public PdfPTable generatePdfTable(int[][] customers, String[] months) {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        addTableHeader(table);
        addRows(table, customers, months);
        return table;
    }

    private void addRows(PdfPTable table, int[][] customers, String[] months) {
        for (int i = 0; i < 12; i++) {
            table.addCell(new PdfPCell(new Phrase(months[i], ReportService.getFont(Font.NORMAL))));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(customers[i][1]))));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(customers[i][2]))));
            table.addCell(new PdfPCell(new Phrase(customers[i][3] + " %")));
        }
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Месяц", "ОКБ", "АКБ", "ЭПБ")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle, ReportService.getFont(Font.NORMAL)));
                    table.addCell(header);
                });
    }
}
