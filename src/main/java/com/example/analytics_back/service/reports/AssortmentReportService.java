package com.example.analytics_back.service.reports;

import com.example.analytics_back.service.ReportService;
import com.example.analytics_back.service.offlineAnalytics.OfflineAssortmentService;
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
public class AssortmentReportService {

    private final OfflineAssortmentService offlineAssortmentService;

    public void printAssortmentChart(Document document) throws DocumentException, IOException {
        document.newPage();
        ReportService.printBoldTextCenter("Анализ ассортимента офлайн магазинов", document);
        document.add(Chunk.NEWLINE);

        Map<String, Object> result = offlineAssortmentService.getAssortment();

        String[] points = (String[]) result.get("points");
        int[] numberOfProducts = (int[]) result.get("numbers");
        ReportService.printImage(createImagePNG(points, numberOfProducts), 600, 350, document);
    }

    private String createImagePNG(String[] points, int[] intRegions) {
        JFreeChart chart = createChart(points, intRegions);

        byte[] chartImageBytes = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(byteArrayOutputStream, chart, 1200, 800);
            chartImageBytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Base64.getEncoder().encodeToString(chartImageBytes);
    }

    private JFreeChart createChart(String[] points, int[] intRegions) {
        CategoryDataset dataset = createDataset(points, intRegions);

        JFreeChart chart = ChartFactory.createBarChart(
                "График ассортимента офлайн магазинов на данный момент",
                "Регион",
                "Номинальное количество",
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

    private CategoryDataset createDataset(String[] points, int[] intRegions) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < points.length; i++) {
            dataset.addValue(intRegions[i], "Номинальное количество", points[i]);
        }
        return dataset;
    }
}
