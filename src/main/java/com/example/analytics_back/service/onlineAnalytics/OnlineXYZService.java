package com.example.analytics_back.service.onlineAnalytics;

import com.example.analytics_back.DTO.analytics.XYZDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Products;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.repo.ProductsRepository;
import com.example.analytics_back.service.UsersService;
import com.example.analytics_back.service.config.DateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OnlineXYZService {

    private final ProductsRepository productsRepository;
    private final UsersService usersService;
    private final DateService dateService;

    public Map<String, Object> xyzFiltered(String date_with, String date_by)
            throws CustomException, ParseException {
        Users user = usersService.getUserInfo();
        if(dateService.compareDate(date_with, date_by)){
            throw new CustomException("Дата начала периода должна быть меньше даты окончания периода!");
        }

        LocalDate with = LocalDate.parse(date_with, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate by = LocalDate.parse(date_by, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        with = with.minusDays(1);
        by = by.plusDays(1);

        Date modified_with = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(with));
        Date modified_by = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(by));

        long millisecondsDifference = modified_by.getTime() - modified_with.getTime();
        int daysDifference = modified_with != modified_by ? (int) (millisecondsDifference / (1000 * 60 * 60 * 24)) - 1 : 0;

        List<Products> products = productsRepository.findAllByOwner(user);
        List<XYZDTO> onlineXYZDTOS = new ArrayList<>();
        List<Products> productsXCategory = new ArrayList<>();
        List<Products> productsYCategory = new ArrayList<>();
        List<Products> productsZCategory = new ArrayList<>();

        for (Products product : products) {
            double revenue = product.getRevenue(modified_with, modified_by);
            double revenueAverage = Math.round(((product.getAverageRevenue(modified_with, modified_by,
                    daysDifference) * 10.0) / 10.0));
            int standardDeviation = product.standardDeviation(modified_with, modified_by, daysDifference);
            int rms = product.rms(modified_with, modified_by, daysDifference);
            String group = "";
            if (rms >= 10 && rms < 25) {
                group = "Y";
                productsYCategory.add(product);
            } else if (rms < 10) {
                group = "Z";
                productsZCategory.add(product);
            } else {
                group = "X";
                productsXCategory.add(product);
            }
            XYZDTO onlineXYZDTO = new XYZDTO(
                    product.getId(), product.getName(), product.getCategory().getName(), revenue,
                    revenueAverage, standardDeviation, rms, group);
            onlineXYZDTOS.add(onlineXYZDTO);
        }
        String descriptionText = generateDescription(productsXCategory, productsYCategory, productsZCategory);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("label", descriptionText);
        resultMap.put("result", onlineXYZDTOS);
        return resultMap;
    }

    private String generateDescription(List<Products> productsXCategory, List<Products> productsYCategory,
                                       List<Products> productsZCategory) {
        String descriptionText = "";
        if (productsXCategory.size() != 0 || productsYCategory.size() != 0 || productsZCategory.size() != 0) {
            descriptionText = "Результаты XYZ анализа говорят о том, что товары:";
        }
        if (productsXCategory.size() != 0) {
            descriptionText = addProductNameToDescription(descriptionText, productsXCategory);
            descriptionText += " имеют устойчивый спрос, их продажи поддаются точному прогнозированию и планированию; ";
        }
        if (productsYCategory.size() != 0) {
            descriptionText = addProductNameToDescription(descriptionText, productsYCategory);
            descriptionText += " имеют колеблющийся спрос, на продажи могли повлиять следующие факторы: " +
                    "сезонность, распродажи, обновления в рекламном продвижении. Закупки следует " +
                    "планировать исходя из трафика; ";
        }
        if (productsZCategory.size() != 0) {
            descriptionText = addProductNameToDescription(descriptionText, productsZCategory);
            descriptionText += " имеют непредсказуемый спрос, спрогнозировать продажи крайне трудно. " +
                    "Количество данных продуктов следует сокращать к минимум и оформлять закупки по " +
                    "мере крайней необходимости; ";
        }
        return  descriptionText;
    }

    private String addProductNameToDescription(String description, List<Products> productsXYZCategory) {
        StringBuilder descriptionBuilder = new StringBuilder(description);
        for (int i = 0; i < productsXYZCategory.size(); i++) {
            descriptionBuilder.append("\"").append(productsXYZCategory.get(i).getName());
            if (i + 1 != productsXYZCategory.size()) {
                descriptionBuilder.append("\", ");
            } else {
                descriptionBuilder.append("\"");
            }
        }
        description = descriptionBuilder.toString();
        return description;
    }
}
