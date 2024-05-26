package com.example.analytics_back.service.onlineAnalytics;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Products;
import com.example.analytics_back.model.Regions;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.service.RegionsService;
import com.example.analytics_back.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OnlineRegionService {
    private final UsersService usersService;
    private final RegionsService regionsService;

    public Map<String, Object> getSalesByRegions() throws CustomException {
        Users users = usersService.getUserInfo();
        List<Products> products = users.getProducts();
        List<Regions> regions = regionsService.getRegions();

        String[] stringRegions = new String[regions.size()];
        int[] intRegions = new int[regions.size()];

        for (int i = 0; i < regions.size(); i++) {
            String temp = regions.get(i).getName();
            stringRegions[i] = temp;
            intRegions[i] += products.stream().reduce(0, (p, product) -> {
                p += product.getDetails().stream().reduce(0, (d, detail) -> {
                    if (detail.getBuy().getPoints().getRegion().getName().equals(temp)) {
                        return d + detail.getQuantity();
                    }
                    return d;
                }, Integer::sum);
                return p;
            }, Integer::sum);

        }
        // Определение региона с наибольшими продажами
        int maxSalesIndex = findMaxIndex(intRegions);
        int minSalesIndex = findMinIndex(intRegions);
        String regionWithMinSales = stringRegions[minSalesIndex];
        String regionWithMaxSales = stringRegions[maxSalesIndex];

        String labelText = generateReportText(regionWithMaxSales,
                intRegions[maxSalesIndex], regionWithMinSales, intRegions[minSalesIndex]);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("regions", stringRegions);
        resultMap.put("numbers", intRegions);
        resultMap.put("labelText", labelText);
        return resultMap;
    }

    private String generateReportText(String regionWithMaxSales, int maxSales, String regionWithMinSales, int minSales) throws CustomException {
        List<Regions> regions = regionsService.getRegions();
        if(regions.size() != 1) {
            return "Проведя региональный анализ продаж в регионе \"" + regionWithMaxSales + "\" был обнаружен потенциал, " +
                    "отражающийся в высоком спросе на вашу продукцию. " + "Следует обратить внимание на пик " +
                    "продаж, достигнутый в этом регионе с числовым значением продаж равным " + maxSales + ". Помимо этого, " +
                    "необходимо не упускать фактор низких продаж в регионе " + regionWithMinSales + ", " +
                    "продажи в котором составляют всего: " + minSales + " шт.";
        }
        else{
            return "Ваши продажи осуществляются всего в одном регионе, этих данных недостаточно для анализа.";
        }
    }

    private int findMaxIndex(int[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private int findMinIndex(int[] array) {
        int minIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[minIndex]) {
                minIndex = i;
            }
        }
        return minIndex;
    }
}
