package com.example.analytics_back.service.reports;

import com.example.analytics_back.DTO.reports.ReportGrowthDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.service.ProductsService;
import com.example.analytics_back.service.ReportService;
import com.example.analytics_back.service.offlineAnalytics.OfflineGrowthService;
import com.example.analytics_back.service.onlineAnalytics.OnlineGrowthService;
import com.example.analytics_back.service.totalAnalytics.TotalGrowthService;
import com.itextpdf.text.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GrowthReportService {

    private final OfflineGrowthService offlineGrowthService;
    private final OnlineGrowthService onlineGrowthService;
    private final TotalGrowthService totalGrowthService;
    private final ProductsService productsService;

    public void printGrowth (Document document, List<ReportGrowthDTO> reportGrowthDTOS, String variant)
            throws DocumentException, IOException, CustomException, ParseException {
        for (ReportGrowthDTO growthDTO: reportGrowthDTOS) {
            document.newPage();
            ReportService.printBoldTextCenter(getNameOfReport(variant), document);

            String startDate = growthDTO.getStartPeriod();
            String endDate = growthDTO.getEndPeriod();
            ReportService.printDefaultText("Товар: " +
                    productsService.getProduct(growthDTO.getProductId()).getName(), document, true);
            ReportService.printDefaultText("С " + startDate +
                    " по " + endDate, document, true);

            Map<String, Object> result = getData(variant, growthDTO.getProductId(), startDate, endDate);

            String[] regions = (String[]) result.get("days");
            double[] numbers = (double[]) result.get("revenues");
            String recommendation = result.get("labelText").toString();
            ReportService.printImage(createImagePNG(regions, numbers), 600, 350, document);
            document.add(Chunk.NEWLINE);
            ReportService.printDefaultText(recommendation, document, false);
        }
    }

    private String createImagePNG(String[] days, double[] revenues) {
        JFreeChart chart = createChart(days, revenues);
        byte[] chartImageBytes = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(byteArrayOutputStream, chart, 1200, 800);
            chartImageBytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Base64.getEncoder().encodeToString(chartImageBytes);
    }

    private JFreeChart createChart(String[] days, double[] revenues) {
        CategoryDataset dataset = createDataset(days, revenues);
        JFreeChart chart = ChartFactory.createLineChart(
                "График темпов продаж",
                "Дата",
                "Темп продаж",
                dataset,
                PlotOrientation.HORIZONTAL,
                false,
                false,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        LineAndShapeRenderer renderer = new LineAndShapeRenderer(true, true);
        plot.setRenderer(renderer);
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(1, Color.GREEN);
        plot.setBackgroundPaint(Color.WHITE);
        return chart;
    }

    private CategoryDataset createDataset(String[] days, double[] revenues) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        WeightedObservedPoints points = new WeightedObservedPoints();
        int displayIndex = 0; // Индекс для отображаемых дат

        for (int i = 0; i < days.length; i++) {
            // Добавляем данные для каждой даты
            points.add(i, revenues[i]);
        }

        double[] coefficients = PolynomialCurveFitter.create(1).fit(points.toList());
        double slope = coefficients[1];
        double intercept = coefficients[0];

        for (int i = 0; i < days.length; i++) {
            // Добавляем данные для каждой даты
            dataset.addValue(revenues[i], "Темп продаж", days[i]);
            dataset.addValue(calculateApproximation(i, slope, intercept), "Аппроксимация", days[i]);

            // Отображаем только каждую десятую метку на оси X
            if (i % 10 != 0) {
                // Если не каждая десятая дата, то скрываем метку
                displayIndex++;
            }
        }

        return dataset;
    }

    private static double calculateApproximation(int index, double slope, double intercept) {
        return slope * index + intercept;
    }

    private Map<String, Object> getData(String variant, Long productId, String startDate, String endDate)
            throws CustomException, ParseException, IOException {
        return switch (variant) {
            case "online" -> onlineGrowthService.growth(productId, startDate, endDate);
            case "offline" -> offlineGrowthService.growth(productId, startDate, endDate);
            case "total" -> totalGrowthService.growth(productId, startDate, endDate);
            default -> null;
        };
    }

    private String getNameOfReport(String variant) {
        return switch (variant) {
            case "online" -> "Анализ темпов продаж по онлайн продажам";
            case "offline" -> "Анализ темпов продаж по оффлайн продажам";
            case "total" -> "Анализ темпов продаж по онлайн и оффлан продажам";
            default -> null;
        };
    }
}
