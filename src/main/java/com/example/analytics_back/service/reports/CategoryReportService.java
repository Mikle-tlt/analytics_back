package com.example.analytics_back.service.reports;

import com.example.analytics_back.DTO.reports.ReportByCategoryDTO;
import com.example.analytics_back.service.CategoriesService;
import com.example.analytics_back.service.ReportService;
import com.example.analytics_back.service.offlineAnalytics.OfflineSalesByCategoryService;
import com.example.analytics_back.service.onlineAnalytics.OnlineSalesByCategoryService;
import com.example.analytics_back.service.totalAnalytics.TotalSalesByCategoryService;
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
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryReportService {
    private final OfflineSalesByCategoryService offlineSalesByCategoryService;
    private final OnlineSalesByCategoryService onlineSalesByCategoryService;
    private final TotalSalesByCategoryService totalSalesByCategoryService;

    private final CategoriesService categoriesService;

    public void printCategory (Document document, List<ReportByCategoryDTO> reportGrowthDTOS, String variant)
            throws DocumentException, IOException {
        for (ReportByCategoryDTO reportByCategoryDTO: reportGrowthDTOS) {
            document.newPage();
            ReportService.printBoldTextCenter(getNameOfReport(variant), document);
            ReportService.printDefaultText("Категория: " +
                    categoriesService.getCategory(reportByCategoryDTO.getCategoryId()).getName(), document, true);
            Map<String, Object> result = getData(variant, reportByCategoryDTO.getCategoryId(), reportByCategoryDTO.getYear());

            int[] quantities = (int[]) result.get("sales");
            String recommendation = result.get("labelText").toString();
            ReportService.printImage(createImagePNG(quantities, reportByCategoryDTO.getYear()), 600, 350, document);
            document.add(Chunk.NEWLINE);
            ReportService.printDefaultText(recommendation, document, false);
        }
    }

    private JFreeChart createChart(int[] quantities, int year) {
        CategoryDataset dataset = createDataset(quantities);

        JFreeChart chart = ChartFactory.createBarChart(
                "График продаж по категории за " + year + " год",
                "Месяц",
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

    private CategoryDataset createDataset(int[] quantities) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < quantities.length; i++) {
            dataset.addValue(quantities[i], "Продажи", getMonthName(i + 1));
        }
        return dataset;
    }

    private String createImagePNG(int[] quantities, int year) {
        JFreeChart chart = createChart(quantities, year);
        byte[] chartImageBytes = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(byteArrayOutputStream, chart, 1200, 800);
            chartImageBytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Base64.getEncoder().encodeToString(chartImageBytes);
    }

    private Map<String, Object> getData(String variant, Long categoryId, int year) {
        return switch (variant) {
            case "online" -> onlineSalesByCategoryService.getSalesByCategory(categoryId, year);
            case "offline" -> offlineSalesByCategoryService.getSalesByCategory(categoryId, year);
            case "total" -> totalSalesByCategoryService.getSalesByCategory(categoryId, year);
            default -> null;
        };
    }

    private String getNameOfReport(String variant) {
        return switch (variant) {
            case "online" -> "Анализ продаж по категории по онлайн продажам";
            case "offline" -> "Анализ продаж по категории по оффлайн продажам";
            case "total" -> "Анализ продаж по категории по онлайн и оффлан продажам";
            default -> null;
        };
    }

    private String getMonthName(int month) {
        return switch (month) {
            case 1 -> "Январь";
            case 2 -> "Февраль";
            case 3 -> "Март";
            case 4 -> "Апрель";
            case 5 -> "Май";
            case 6 -> "Июнь";
            case 7 -> "Июль";
            case 8 -> "Август";
            case 9 -> "Сентябрь";
            case 10 -> "Октябрь";
            case 11 -> "Ноябрь";
            case 12 -> "Декабрь";
            default -> throw new IllegalArgumentException("Неверный месяц: " + month);
        };
    }
}
