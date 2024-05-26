package com.example.analytics_back.service;

import com.example.analytics_back.DTO.ReportDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.reports.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.function.ToDoubleFunction;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {
    private final ABCReportService abcReportService;
    private final GeneralReportService generalReportService;
    private final XYZReportService xyzReportService;

    private final ProfitabilityReportService profitabilityReportService;

    private final RegionReportService regionReportService;
    private final GrowthReportService growthReportService;
    private final CategoryReportService categoryReportService;

    private final CustomerReportService customerReportService;
    private final AssortmentReportService assortmentReportService;

    public void downloadPdf(ReportDTO reportDTO) throws IOException, DocumentException, CustomException, ParseException {
        byte[] pdfBytes = generatePdf(reportDTO);
        String fileName = generateFileName();
        String filePath = "src/main/resources/templates/" + fileName;
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(pdfBytes);
        }
    }

    private String generateFileName() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        return "Statistic_report_" + now.format(formatter) + ".pdf";
    }

    private byte[] generatePdf(ReportDTO reportDTO) throws IOException, DocumentException, CustomException, ParseException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            if (reportDTO.getOfflineABC() != null)
                abcReportService.printABC(document, reportDTO.getOfflineABC(), "offline");
            if (reportDTO.getOnlineABC() != null)
                abcReportService.printABC(document, reportDTO.getOnlineABC(), "online");
            if (reportDTO.getTotalABC() != null)
                abcReportService.printABC(document, reportDTO.getTotalABC(), "total");

            if (reportDTO.getOfflineGeneral() != null)
                generalReportService.printGeneral(document, reportDTO.getOfflineGeneral(), "offline");
            if (reportDTO.getOnlineGeneral() != null)
                generalReportService.printGeneral(document, reportDTO.getOnlineGeneral(), "online");
            if (reportDTO.getTotalGeneral() != null)
                generalReportService.printGeneral(document, reportDTO.getTotalGeneral(), "total");

            if (reportDTO.getOfflineXYZ() != null)
                xyzReportService.printXYZ(document, reportDTO.getOfflineXYZ(), "offline");
            if (reportDTO.getOnlineXYZ() != null)
                xyzReportService.printXYZ(document, reportDTO.getOnlineXYZ(), "online");
            if (reportDTO.getTotalXYZ() != null)
                xyzReportService.printXYZ(document, reportDTO.getTotalXYZ(), "total");

            if (reportDTO.getOfflineProfitability() != null)
                profitabilityReportService.printProfitability(document, reportDTO.getOfflineProfitability(), "offline");
            if (reportDTO.getOnlineProfitability() != null)
                profitabilityReportService.printProfitability(document, reportDTO.getOnlineProfitability(), "online");
            if (reportDTO.getTotalProfitability() != null)
                profitabilityReportService.printProfitability(document, reportDTO.getTotalProfitability(), "total");

            if (reportDTO.isOfflineRegion())
                regionReportService.printRegionChart(document,"offline");
            if (reportDTO.isOnlineRegion())
                regionReportService.printRegionChart(document,"online");
            if (reportDTO.isTotalRegion())
                regionReportService.printRegionChart(document, "total");

            if (reportDTO.getOfflineGrowth() != null)
                growthReportService.printGrowth(document, reportDTO.getOfflineGrowth(), "offline");
            if (reportDTO.getOnlineGrowth() != null)
                growthReportService.printGrowth(document, reportDTO.getOnlineGrowth(), "online");
            if (reportDTO.getTotalGrowth() != null)
                growthReportService.printGrowth(document, reportDTO.getTotalGrowth(), "total");

            if (reportDTO.getOfflineByCategory() != null)
                categoryReportService.printCategory(document, reportDTO.getOfflineByCategory(), "offline");
            if (reportDTO.getOnlineByCategory() != null)
                categoryReportService.printCategory(document, reportDTO.getOnlineByCategory(), "online");
            if (reportDTO.getTotalByCategory() != null)
                categoryReportService.printCategory(document, reportDTO.getTotalByCategory(), "total");

            if (reportDTO.getOnlineCustomers() != null)
                customerReportService.printCustomer(document, reportDTO.getOnlineCustomers());
            if (reportDTO.isOfflineAssortment())
                assortmentReportService.printAssortmentChart(document);

            document.close();
            return byteArrayOutputStream.toByteArray();
        }
    }

    public static void printBoldTextCenter(String name, Document document) throws DocumentException {
        Paragraph paragraph = new Paragraph("\n" + name, getFont(Font.BOLD));
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);
    }

    public static void printDefaultText(String text, Document document, boolean alignment) throws DocumentException{
        Paragraph paragraph = new Paragraph(text, getFont(Font.NORMAL));
        if (alignment) paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);
    }

    public static <T> int calculateSum(List<T> objects, ToDoubleFunction<T> fieldExtractor) {
        int sum = 0;
        for (T obj : objects) {
            sum += fieldExtractor.applyAsDouble(obj);
        }
        return sum;
    }

    public static Font getFont(int font) {
        BaseFont baseFont;
        try {
            baseFont = BaseFont.createFont("src/main/resources/static/timesnewromanpsmt.ttf",
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
        return new Font(baseFont, 10, font);
    }

    public static void printImage(String image, float width, float height, Document document) throws DocumentException, IOException {
        byte[] chartImageBytes;
        chartImageBytes = Base64.getDecoder().decode(image);
        Image chartImage = com.itextpdf.text.Image.getInstance(chartImageBytes);
        chartImage.scaleToFit(width, height);
        document.add(chartImage);
    }
}
