package com.example.analytics_back.service.reports;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.ReportService;
import com.example.analytics_back.service.offlineAnalytics.OfflineRegionService;
import com.example.analytics_back.service.onlineAnalytics.OnlineRegionService;
import com.example.analytics_back.service.totalAnalytics.TotalRegionService;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegionReportService {

    private final OfflineRegionService offlineRegionService;
    private final OnlineRegionService onlineRegionService;
    private final TotalRegionService totalRegionService;

    public void printRegionChart(Document document, String variant) throws DocumentException, IOException, CustomException {
        document.newPage();
        ReportService.printBoldTextCenter(getNameOfReport(variant), document);
        document.add(Chunk.NEWLINE);

        Map<String, Object> result = getData(variant);

        String[] regions = (String[]) result.get("regions");
        int[] numbers = (int[]) result.get("numbers");
        String recommendation = result.get("labelText").toString();
        ReportService.printImage(createImagePNG(regions, numbers), 600, 350, document);
        document.add(Chunk.NEWLINE);
        ReportService.printDefaultText(recommendation, document, false);
    }

    private String createImagePNG(String[] regions, int[] intRegions) {
        JFreeChart chart = createChart(regions, intRegions);

        byte[] chartImageBytes = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(byteArrayOutputStream, chart, 1200, 800);
            chartImageBytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Base64.getEncoder().encodeToString(chartImageBytes);
    }

    private JFreeChart createChart(String[] regions, int[] intRegions) {
        CategoryDataset dataset = createDataset(regions, intRegions);
        JFreeChart chart = ChartFactory.createBarChart(
                "График регионального анализа продаж за текущий год",
                "Регион",
                "Количество",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                false,
                false
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        plot.setBackgroundPaint(Color.WHITE);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);

        return chart;
    }

    private CategoryDataset createDataset(String[] regions, int[] intRegions) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < regions.length; i++) {
            dataset.addValue(intRegions[i], "Продажи", regions[i]);
        }
        return dataset;
    }

    private Map<String, Object> getData(String variant) throws CustomException {
        return switch (variant) {
            case "online" -> onlineRegionService.getSalesByRegions();
            case "offline" -> offlineRegionService.getSalesByRegions();
            case "total" -> totalRegionService.getSalesByRegions();
            default -> null;
        };
    }

    private String getNameOfReport(String variant) {
        return switch (variant) {
            case "online" -> "Региональный анализ по онлайн продажам";
            case "offline" -> "Региональный анализ по оффлайн продажам";
            case "total" -> "Региональный анализ по онлайн и оффлан продажам";
            default -> null;
        };
    }
}
