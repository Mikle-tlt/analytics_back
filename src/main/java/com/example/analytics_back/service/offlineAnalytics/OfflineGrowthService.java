package com.example.analytics_back.service.offlineAnalytics;

import com.example.analytics_back.DTO.analytics.TrendResult;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Products;
import com.example.analytics_back.repo.ProductsRepository;
import com.example.analytics_back.service.config.DateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Service
@Slf4j
@RequiredArgsConstructor
public class OfflineGrowthService {
    private final ProductsRepository productsRepository;
    private final DateService dateService;
    protected long ONE_DAY = 1000 * 60 * 60 * 24;
    public Map<String, Object> growth(Long productId, String date_with, String date_by)
            throws IOException, ParseException, CustomException {
        if(dateService.compareDate(date_with, date_by)){
            throw new CustomException("Дата начала периода должна быть меньше даты окончания периода!");
        }

        Products product = productsRepository.getReferenceById(productId);

        Date with = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(date_with));
        Date by = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(date_by));

        double[] revenues = new double[(int) ((by.getTime() - with.getTime() + ONE_DAY) / ONE_DAY)];
        String[] days = new String[(int) ((by.getTime() - with.getTime() + ONE_DAY) / ONE_DAY)];

        with.setTime(with.getTime() - ONE_DAY);
        by.setTime(by.getTime() + ONE_DAY);

        for (int i = 0; i < revenues.length; i++) {
            Date day = new Date();
            day.setTime(with.getTime() + ONE_DAY);
            days[i] = new SimpleDateFormat("yyyy-MM-dd").format(day);

            Date date1 = new Date();
            Date date2 = new Date();

            date1.setTime(with.getTime());
            date2.setTime(with.getTime() + ONE_DAY * 2);

            double income1 = product.getRevenueOffline(date1, date2);

            date1.setTime(with.getTime());
            date2.setTime(with.getTime() + ONE_DAY * 4);

            double income2 = product.getRevenueOffline(date1, date2);

            if (income2 == 0) {
                revenues[i] = income1 * 100;
            } else {
                revenues[i] = (income1 / income2) * 100;
            }

            with.setTime(with.getTime() + ONE_DAY);
        }
        TrendResult trendResult = detectTrend(days, revenues);
        String trendDirection = trendResult.getTrendDirection();
        String labelText = "В итоге анализа темпов продаж по выбранной позиции за данный промежуток времени " +
                trendDirection;
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("revenues", revenues);
        resultMap.put("days", days);
        resultMap.put("labelText", labelText);
        return  resultMap;
    }
    private TrendResult detectTrend(String[] days, double[] incomes) {
        double[] xValues = new double[days.length];
        double[] yValues = new double[incomes.length];

        for (int i = 0; i < days.length; i++) {
            xValues[i] = i; // Индексы дней
            yValues[i] = incomes[i];
        }
        SimpleRegression regression = new SimpleRegression();
        for (int i = 0; i < xValues.length; i++) {
            regression.addData(xValues[i], yValues[i]);
        }
        double slope = regression.getSlope();
        double intercept = regression.getIntercept();
        return new TrendResult(slope, intercept);
    }
}
