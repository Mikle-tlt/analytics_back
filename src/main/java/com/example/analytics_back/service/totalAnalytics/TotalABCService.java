package com.example.analytics_back.service.totalAnalytics;

import com.example.analytics_back.DTO.analytics.ABCDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.*;
import com.example.analytics_back.repo.DetailsRepository;
import com.example.analytics_back.repo.OfflineDetailsRepository;
import com.example.analytics_back.repo.ProductsRepository;
import com.example.analytics_back.service.UsersService;
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
public class TotalABCService {
    private final ProductsRepository productsRepository;
    private final DetailsRepository detailsRepository;
    private final UsersService usersService;
    private final OfflineDetailsRepository offlineDetailsRepository;

    public Map<String, Object> abc() throws CustomException {
        Users user = usersService.getUserInfo();
        List<Products> products = productsRepository.findAllByOwner(user);
        List<ABCDTO> totalABCDTOS = new ArrayList<>();
        List<Products> productsACategory = new ArrayList<>();
        List<Products> productsBCategory = new ArrayList<>();
        List<Products> productsCCategory = new ArrayList<>();
        double allDifference = 0;
        for (Products product : products) {
            double onlineDifference = product.getDifferent();
            double offlineDifference = product.getDifferentOffline();
            allDifference += onlineDifference;
            allDifference += offlineDifference;
        }
        for (Products product : products) {
            int onlineQuantity = detailsRepository.findAll().stream()
                    .filter(detail -> detail.getProduct() != null &&
                            detail.getProduct().getId().equals(product.getId()))
                    .mapToInt(Details::getQuantity)
                    .sum();
            int offlineQuantity = offlineDetailsRepository.findAll().stream()
                    .filter(detail -> {
                        OfflinePointProducts offlinePointProducts = detail.getOfflinePointProducts();
                        return offlinePointProducts != null &&
                                offlinePointProducts.getProduct() != null &&
                                offlinePointProducts.getProduct().getId().equals(product.getId());
                    })
                    .mapToInt(OfflineDetails::getQuantity)
                    .sum();
            int totalQuantity = onlineQuantity + offlineQuantity;
            double totalRevenue = product.getRevenue() + product.getRevenueOffline();
            double totalCost = product.getCostPrice() + product.getCostPriceOffline();
            double totalDifference = product.getDifferent() + product.getDifferentOffline();
            double profitShare = Math.round((((totalDifference * 100) / allDifference) * 10.0) / 10.0);
            String group = "";
            if (profitShare  >= 10 && profitShare < 80) {
                group = "B";
                productsBCategory.add(product);
            } else if (profitShare < 10) {
                group = "C";
                productsCCategory.add(product);
            } else {
                group = "A";
                productsACategory.add(product);
            }

            ABCDTO totalABCDTO = new ABCDTO(
                    product.getId(), product.getName(), product.getCategory().getName(), group, profitShare,
                    totalQuantity, totalCost, totalRevenue, totalDifference);
            totalABCDTOS.add(totalABCDTO);
        }
        String labelText = generateRecommendations(productsACategory, productsBCategory, productsCCategory);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("label", labelText);
        resultMap.put("result", totalABCDTOS);

        return resultMap;
    }
    public Map<String, Object> abcFiltered(String date_with, String date_by)
            throws CustomException, ParseException {
        Users user = usersService.getUserInfo();
        if(compareDate(date_with, date_by)){
            throw new CustomException("Дата начала периода должна быть меньше даты окончания периода!");
        }

        LocalDate with = LocalDate.parse(date_with, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate by = LocalDate.parse(date_by, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        with = with.minusDays(1);
        by = by.plusDays(1);

        Date modified_with = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(with));
        Date modified_by = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(by));

        List<Products> products = productsRepository.findAllByOwner(user);
        List<ABCDTO> totalABCDTOS = new ArrayList<>();
        List<Products> productsACategory = new ArrayList<>();
        List<Products> productsBCategory = new ArrayList<>();
        List<Products> productsCCategory = new ArrayList<>();
        double allDifference = 0;
        for (Products product : products) {
            double onlineDifference = product.getDifferent(modified_with, modified_by);
            double offlineDifference = product.getDifferentOffline(modified_with, modified_by);
            allDifference += onlineDifference;
            allDifference += offlineDifference;
        }
        for (Products product : products) {
            int onlineQuantity = detailsRepository.findAll().stream()
                    .filter(detail -> detail.getProduct() != null &&
                            detail.getProduct().getId().equals(product.getId()) &&
                            detail.getBuy().getOriginDate().after(modified_with) &&
                            detail.getBuy().getOriginDate().before(modified_by))
                    .mapToInt(Details::getQuantity)
                    .sum();
            int offlineQuantity = offlineDetailsRepository.findAll().stream()
                    .filter(detail -> {
                        OfflinePointProducts offlinePointProducts = detail.getOfflinePointProducts();
                        return offlinePointProducts != null &&
                                offlinePointProducts.getProduct() != null &&
                                detail.getOfflinePointProducts().getProduct().getId().equals(product.getId()) &&
                                detail.getOfflineBuy().getOriginDate().after(modified_with) &&
                                detail.getOfflineBuy().getOriginDate().before(modified_by);
                    })
                    .mapToInt(OfflineDetails::getQuantity)
                    .sum();
            int totalQuantity = onlineQuantity + offlineQuantity;
            double totalRevenue = product.getRevenue(modified_with, modified_by) +
                    product.getRevenueOffline(modified_with, modified_by);
            double totalCost = product.getCostPrice(modified_with, modified_by) +
                    product.getCostPriceOffline(modified_with, modified_by);
            double totalDifference = product.getDifferent(modified_with, modified_by) +
                    product.getDifferentOffline(modified_with, modified_by);

            double profitShare = Math.round((((totalDifference * 100) / allDifference) * 10.0) / 10.0);
            String group = "";

            if (profitShare  >= 10 && profitShare < 80) {
                group = "B";
                productsBCategory.add(product);
            } else if (profitShare < 10) {
                group = "C";
                productsCCategory.add(product);
            } else {
                group = "A";
                productsACategory.add(product);
            }

            ABCDTO totalABCDTO = new ABCDTO(
                    product.getId(), product.getName(), product.getCategory().getName(), group, profitShare,
                    totalQuantity, totalCost, totalRevenue, totalDifference);
            totalABCDTOS.add(totalABCDTO);
        }
        String labelText = generateRecommendations(productsACategory, productsBCategory, productsCCategory);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("label", labelText);
        resultMap.put("result", totalABCDTOS);
        return resultMap;
    }
    public boolean compareDate(String with, String by) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = sdf.parse(by);
        Date date2 = sdf.parse(with);
        return date2.after(date1);
    }
    public String generateRecommendations(List<Products> categoryA,
                                          List<Products> categoryB,
                                          List<Products> categoryC) {
        String description = "";
        if (!categoryA.isEmpty()) {
            description += "Рекомендуется активное управление запасами и уделение повышенного внимания товаров, " +
                    "находящихся в категории \"А\": " + printProductNames(categoryA) + "\n";
        } else {
            description += "В категории \"А\" товары отсутствуют\n";
        }
        if (!categoryB.isEmpty()) {
            description += "Рекомендуется стандартное управление запасами для товаров, находящихся в категории \"B\": "
                    + printProductNames(categoryB) + "\n";
        } else {
            description += "В категории \"B\" товары отсутствуют\n";
        }
        if (!categoryC.isEmpty()) {
            description += "Рекомендуется снизить запасы следующих товаров, находящихся в категории \"C\": "
                    + printProductNames(categoryC) + "\n";
        } else {
            description += "В категории \"С\" товары отсутствуют\n";
        }
        return description;
    }

    private String printProductNames(List<Products> products) {

        StringBuilder text = new StringBuilder();
        for (int i = 0; i < products.size(); i++) {
            Products product = products.get(i);
            if (i < products.size() - 1) {
                text.append("\"" + product.getName() + "\"").append("; ");
            } else {
                text.append("\"" + product.getName() + "\"").append(". ");
            }
        }
        return text.toString();
    }
}
