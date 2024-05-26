package com.example.analytics_back.service.onlineAnalytics;

import com.example.analytics_back.model.Categories;
import com.example.analytics_back.model.Products;
import com.example.analytics_back.service.CategoriesService;
import com.example.analytics_back.service.ProductsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OnlineSalesByCategoryService {

    private final CategoriesService categoriesService;
    private final ProductsService productsService;
    public Map<String, Object> getSalesByCategory(Long categoryId, int year) {

        int[] quantities = new int[12];

        Categories category = categoriesService.getCategory(categoryId);

        for (Products p : category.getProducts()) {
            Products product = productsService.getProduct(p.getId());
            for (int i = 0; i < quantities.length; i++) {
                int finalI = i;
                quantities[i] += product.getDetails().stream().reduce(0, (integer, detail) -> {
                    String date;
                    if (finalI < 9) date = year + "-0" + (finalI + 1);
                    else date = year + "-" + (finalI + 1);
                    if (detail.getBuy().getDate().startsWith(date)) return integer + detail.getQuantity();
                    return integer;
                }, Integer::sum);
            }
        }

        boolean isEmpty = true;
        for (int quantity : quantities) {
            if (quantity > 0) {
                isEmpty = false;
                break;
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("sales", quantities);
        resultMap.put("labelText", generateDescription(quantities, category.getName(), isEmpty));
        return resultMap;
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

    private String generateDescription (int[] monthlySales, String category, boolean isEmpty){
        String description;
        if (isEmpty)
            return "В выбранном году продаж по категории \"" + category + "\" не обнаружено.";
        description = analyzeOverallTrends(monthlySales);
        description += analyzeSeasonalPatterns(monthlySales);
        description += forecastFutureSales(monthlySales);
        description += "\n" +  analyzeOverallTrendsLinearRegression(monthlySales);

        return description;
    }

    // Анализ общих тенденций продаж
    private String analyzeOverallTrends(int[] monthlySales) {
        int totalSales = calculateTotalSales(monthlySales);
        double averageSales = calculateAverageSales(monthlySales);

        String trendsDescr = "Сумма продаж по данной категории за 12 месяцев: " + totalSales + ".";
        trendsDescr += "\n" + "В среднем продаж в месяц: " + formatNumber(averageSales) + ".\n";
        return trendsDescr;
    }

    // Анализ общих тенденций продаж с использованием линейной регрессии
    private String analyzeOverallTrendsLinearRegression(int[] monthlySales) {
        String linearRegressionDescr;

        linearRegressionDescr = "\nАнализ общих тенденций продаж (в соответствии с линейной регрессией).\n";

        // Создание объекта линейной регрессии
        SimpleRegression regression = new SimpleRegression(true);

        // Заполнение данных в регрессию
        for (int i = 0; i < monthlySales.length; i++) {
            regression.addData(i + 1, monthlySales[i]);
        }

        // Получение коэффициентов регрессии
        double slope = regression.getSlope();
        double intercept = regression.getIntercept();

        linearRegressionDescr += "Коэффициент наклона (slope): " + formatNumber(slope) + ".\n";
        linearRegressionDescr += "Коэффициент пересечения (intercept): " + formatNumber(intercept) + ".\n";

        // Определение тенденции продаж
        if (slope > 0) {
            linearRegressionDescr += "За год наблюдается возрастающая тенденция в продажах выбранной категории.";
        } else if (slope < 0) {
            linearRegressionDescr += "За год наблюдается спадающая тенденция в продаж выбранной категории.";
        } else {
            linearRegressionDescr += "Продажи по данной категории стабильны за выбранный год.";
        }

        return linearRegressionDescr;
    }

    // Анализ сезонных паттернов
    private String analyzeSeasonalPatterns(int[] monthlySales) {
        java.util.List<Integer> maxIndices = findIndicesOfMaxValues(monthlySales);
        java.util.List<Integer> minIndices = findIndicesOfMinValues(monthlySales);

        StringBuilder patternsDescription = new StringBuilder();

        if (!maxIndices.isEmpty()) {
            patternsDescription.append("Месяц(ы) с наивысшими продажами: ");
            for (int i = 0; i < maxIndices.size(); i++) {
                if(i + 1 != maxIndices.size()){
                patternsDescription.append(getMonthName(i + 1)).append(", ");
                }
                else{
                    patternsDescription.append(getMonthName(i + 1)).append(".");
                }
            }
            patternsDescription.append("\n");
        }

        if (!minIndices.isEmpty()) {
            patternsDescription.append("Месяц(ы) с наименьшими продажами: ");
            for (int i = 0; i < minIndices.size(); i++) {
                if(i + 1 != minIndices.size()) {
                    patternsDescription.append(getMonthName(i + 1)).append(", ");
                }
                else{
                    patternsDescription.append(getMonthName(i + 1)).append(".");
                }
            }
            patternsDescription.append("\n");
        }
        return patternsDescription.toString();
    }

    private java.util.List<Integer> findIndicesOfMaxValues(int[] array) {
        java.util.List<Integer> indices = new ArrayList<>();
        int maxValue = Integer.MIN_VALUE;

        for (int i = 0; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
                indices.clear();
                indices.add(i);
            } else if (array[i] == maxValue) {
                indices.add(i);
            }
        }

        return indices;
    }

    // Нахождение индексов минимальных значений в массиве
    private java.util.List<Integer> findIndicesOfMinValues(int[] array) {
        List<Integer> indices = new ArrayList<>();
        int minValue = Integer.MAX_VALUE;

        for (int i = 0; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
                indices.clear();
                indices.add(i);
            } else if (array[i] == minValue) {
                indices.add(i);
            }
        }

        return indices;
    }

    // Прогнозирование будущих продаж с использованием экспоненциального сглаживания
    private String forecastFutureSales(int[] monthlySales) {
        String forecastSales;
        forecastSales = "\nПрогноз продаж на следующий год (в соответствии с экспоненциальным сглаживанием) - \n";
        double alpha = 0.2;
        double[] forecastedSales = exponentialSmoothing(monthlySales, alpha);
        for (int i = 0; i < forecastedSales.length; i++) {
            if(i + 1 != forecastedSales.length) {
                forecastSales += getMonthName(i + 1) + ": " + formatNumber(forecastedSales[i]) + "; ";
            }
            else{
                forecastSales += getMonthName(i + 1) + ": " + formatNumber(forecastedSales[i]) + ". ";
            }
        }
        return  forecastSales;
    }

    // Расчет общих продаж
    private int calculateTotalSales(int[] monthlySales) {
        return Arrays.stream(monthlySales).sum();
    }

    // Расчет средних продаж
    private double calculateAverageSales(int[] monthlySales) {
        return calculateTotalSales(monthlySales) / (double) monthlySales.length;
    }

    // Применение экспоненциального сглаживания
    private double[] exponentialSmoothing(int[] data, double alpha) {
        double[] smoothedData = new double[data.length];

        smoothedData[0] = data[0]; // Начальное значение равно первому значению данных

        for (int i = 1; i < data.length; i++) {
            smoothedData[i] = alpha * data[i] + (1 - alpha) * smoothedData[i - 1];
        }

        return smoothedData;
    }

    // Форматирование числа с ограничением до одной десятой
    private static String formatNumber(double number) {
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(number);
    }
}
