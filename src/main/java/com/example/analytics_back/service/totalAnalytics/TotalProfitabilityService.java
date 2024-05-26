package com.example.analytics_back.service.totalAnalytics;

import com.example.analytics_back.DTO.analytics.ProfitabilityDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Products;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.service.UsersService;
import com.example.analytics_back.service.config.DateService;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TotalProfitabilityService {
    private final DateService dateService;
    private final UsersService usersService;

    public Map<String, Object> profitabilityData(String date_with_f, String date_by_f,
                                                 String date_with_s, String date_by_s)
            throws DocumentException, IOException, CustomException, ParseException {
        Users user = usersService.getUserInfo();
        if(dateService.compareDate(date_with_f, date_by_f) || dateService.compareDate(date_with_s, date_by_s)){
            throw new CustomException("Дата начала периода должна быть меньше даты окончания периода!");
        }
        if(date_with_f.matches(date_with_s) && date_by_f.matches(date_by_s)) {
            throw new CustomException("Вы выбрали одинаковые периоды для анализа!");
        }
        List<Products> products = user.getProducts();
        Map<String, Date> firstPeriod = dateService.getDates(date_with_f, date_by_f);
        Date date_with_f_mod = firstPeriod.get("with");
        Date date_by_f_mod = firstPeriod.get("by");
        Map<String, Date> secondPeriod = dateService.getDates(date_with_s, date_by_s);
        Date date_with_s_mod = secondPeriod.get("with");
        Date date_by_s_mod = secondPeriod.get("by");

        List<ProfitabilityDTO> profitabilities =
                getDataProfitability(products, date_with_f_mod, date_by_f_mod, date_with_s_mod, date_by_s_mod);
        String labelText = calculateProfitability(products, date_with_f_mod, date_by_f_mod, date_with_s_mod, date_by_s_mod);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", profitabilities);
        resultMap.put("label", labelText);
        return resultMap;
    }

    private List<ProfitabilityDTO> getDataProfitability(List<Products> products, Date firstBegin, Date firstEnd,
                                                        Date secondBegin, Date secondEnd) {
        List<ProfitabilityDTO> onlineProfitabilityDTOS = new ArrayList<>();
        for (Products product : products) {
            int firstPeriod = 0;
            if (product.getRevenue(firstBegin, firstEnd) != 0 ||
                    product.getRevenueOffline(firstBegin, firstEnd) != 0) {
                firstPeriod = (int) ((product.getDifferent(firstBegin, firstEnd) +
                        product.getDifferentOffline(firstBegin, firstEnd)) * 100 /
                        (product.getRevenue(firstBegin, firstEnd) + product.getRevenueOffline(firstBegin, firstEnd)));
            }
            int secondPeriod = 0;
            if (product.getRevenue(secondBegin, secondEnd) != 0 ||
                    product.getRevenueOffline(secondBegin, secondEnd) != 0) {
                secondPeriod = (int) ((product.getDifferent(secondBegin, secondEnd) +
                        product.getDifferentOffline(secondBegin, secondEnd)) * 100 /
                        (product.getRevenue(secondBegin, secondEnd) + product.getRevenueOffline(secondBegin, secondEnd)));
            }
            ProfitabilityDTO onlineProfitabilityDTO = new ProfitabilityDTO(
                    product.getId(), product.getName(), product.getCategory().getName(), firstPeriod, secondPeriod);
            onlineProfitabilityDTOS.add(onlineProfitabilityDTO);
        }
        return onlineProfitabilityDTOS;
    }

    private String calculateProfitability(List<Products> products, Date firstBegin, Date firstEnd,
                                          Date secondBegin, Date secondEnd) {
        List<Products> productsReduction = new ArrayList<>();
        List<Products> productsIncrease = new ArrayList<>();
        List<Products> productsStability = new ArrayList<>();

        List<Integer> isIncrease = getIsIncrease(products, firstBegin, firstEnd, secondBegin, secondEnd);;
        for (int i = 0; i < isIncrease.size(); i++) {
            if (isIncrease.get(i) == 1) {
                productsIncrease.add(products.get(i));
            } else if (isIncrease.get(i) == -1) {
                productsReduction.add(products.get(i));
            } else{
                productsStability.add(products.get(i));
            }
        }
        String description = "";
        if(productsReduction.size() != 0 || productsIncrease.size() != 0 || productsStability.size() != 0) {
            description += "Результаты анализа рентабильности показывают, что товары: ";
        }
        if (productsReduction.size() != 0) {
            description = generateDescription(description, productsReduction);
            description += " имеют тенденцию по снижению рентабильности в сравниваемые периоды, что может быть " +
                    "вызвано падением цен отпуска товаров или повышением их себестоимости; ";
        }
        if(productsIncrease.size() != 0){
            description = generateDescription(description, productsIncrease);
            description += " показали рост рентабильности, на что, в свою очередь, следует обратить внимание, " +
                    "выяснить какие факторы на это повлияли и увеличить силу их воздействия; ";
        }
        if(productsStability.size() != 0){
            description = generateDescription(description, productsStability);
            description += " не имеют колебаний в ренталильности в сравниваемые периоды; ";
        }
        return description;
    }

    private List<Integer> getIsIncrease(List<Products> products, Date firstBegin, Date firstEnd,
                                        Date secondBegin, Date secondEnd){
        List<Integer> isIncrease = new ArrayList<>();
        for (Products product : products) {
            double first = 0;
            double second = 0;

            double firstRevenue = product.getRevenue(firstBegin, firstEnd) + product.getRevenueOffline(firstBegin, firstEnd);
            double secondRevenue = product.getRevenue(secondBegin, secondEnd) + product.getRevenueOffline(secondBegin, secondEnd);

            if (firstRevenue != 0) {
                first = ((product.getDifferent(firstBegin, firstEnd) + product.getDifferentOffline(firstBegin, firstEnd)) * 100) / firstRevenue;
            } else {
                first = (product.getDifferent(firstBegin, firstEnd) + product.getDifferentOffline(firstBegin, firstEnd)) * 100;
            }

            if (secondRevenue != 0) {
                second = ((product.getDifferent(secondBegin, secondEnd) + product.getDifferentOffline(secondBegin, secondEnd)) * 100) / secondRevenue;
            } else {
                second = (product.getDifferent(secondBegin, secondEnd) + product.getDifferentOffline(secondBegin, secondEnd)) * 100;
            }

            if (first > second) {
                isIncrease.add(-1);
            } else if (first < second) {
                isIncrease.add(1);
            } else {
                isIncrease.add(0);
            }
        }
        return isIncrease;
    }

    private String generateDescription(String description, List<Products> productsCategory) {
        StringBuilder descriptionBuilder = new StringBuilder(description);
        for (int i = 0; i < productsCategory.size(); i++) {
            descriptionBuilder.append("\"").append(productsCategory.get(i).getName());
            if (i + 1 != productsCategory.size()) {
                descriptionBuilder.append("\", ");
            } else {
                descriptionBuilder.append("\"");
            }
        }
        description = descriptionBuilder.toString();
        return description;
    }
}
