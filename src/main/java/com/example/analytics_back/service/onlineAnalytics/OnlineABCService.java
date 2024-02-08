package com.example.analytics_back.service.onlineAnalytics;

import com.example.analytics_back.DTO.onlineAnalytics.OnlineABCDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Details;
import com.example.analytics_back.model.Products;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.repo.DetailsRepository;
import com.example.analytics_back.repo.ProductsRepository;
import com.example.analytics_back.repo.UsersRepository;
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
public class OnlineABCService {
    private final UsersRepository usersRepository;
    private final ProductsRepository productsRepository;
    private final DetailsRepository detailsRepository;
    public Map<String, Object>  abc(Long userId) throws CustomException {
        if(!usersRepository.existsById(userId)){
            throw new CustomException("Пользователь не обнаружен, невозможно получить данные!");
        }
        Users users = usersRepository.findById(userId).orElseThrow();
        List<Products> products = productsRepository.findAllByOwner(users);
        List<OnlineABCDTO> onlineABCDTOS = new ArrayList<>();
        List<Products> productsACategory = new ArrayList<>();
        List<Products> productsBCategory = new ArrayList<>();
        List<Products> productsCCategory = new ArrayList<>();
        double allDifference = 0;
        for (Products product : products) {
            double difference = product.getDifferent();
            allDifference += difference;
        }
        for (Products product : products) {
            int quantity = detailsRepository.findAll().stream()
                    .filter(detail -> detail.getProduct() != null && detail.getProduct().getId().equals(product.getId()))
                    .mapToInt(Details::getQuantity)
                    .sum();
            double revenue = product.getRevenue();
            double cost = product.getCostPrice();
            double difference = product.getDifferent();
            double profitShare = Math.round((((difference * 100) / allDifference) * 10.0) / 10.0);
            String group = "";
            if (profitShare  >= 10 && profitShare < 80) {
                group = "B";
                productsBCategory.add(product);
            } else if (profitShare < 10 && profitShare != 0) {
                group = "С";
                productsCCategory.add(product);
            } else if (profitShare > 80){
                group = "A";
                productsACategory.add(product);
            }

            OnlineABCDTO onlineABCDTO = new OnlineABCDTO(
                    product.getId(), product.getName(), product.getCategory().getName(), group, profitShare,
                    quantity, cost, revenue, difference);
            onlineABCDTOS.add(onlineABCDTO);
        }
        String labelText = generateRecommendations(productsACategory, productsBCategory, productsCCategory);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("labelText", labelText);
        resultMap.put("onlineABCDTOS", onlineABCDTOS);

        return resultMap;
    }
    public Map<String, Object> abcFiltered(Long userId, String date_with, String date_by)
            throws CustomException, ParseException {
        if(!usersRepository.existsById(userId)){
            throw new CustomException("Пользователь не обнаружен, невозможно получить данные!");
        }
        Users users = usersRepository.findById(userId).orElseThrow();
        if(compareDate(date_with, date_by)){
            throw new CustomException("Дата начала периода должна быть меньше даты окончания периода!");
        }

        LocalDate with = LocalDate.parse(date_with, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate by = LocalDate.parse(date_by, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        with = with.minusDays(1);
        by = by.plusDays(1);

        Date modified_with = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(with));
        Date modified_by = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(by));

        List<Products> products = productsRepository.findAllByOwner(users);
        List<OnlineABCDTO> onlineABCDTOS = new ArrayList<>();
        List<Products> productsACategory = new ArrayList<>();
        List<Products> productsBCategory = new ArrayList<>();
        List<Products> productsCCategory = new ArrayList<>();
        double allDifference = 0;
        for (Products product : products) {
            double difference = product.getDifferent(modified_with, modified_by);
            allDifference += difference;
        }
        for (Products product : products) {
            int quantity = detailsRepository.findAll().stream()
                    .filter(detail -> detail.getProduct() != null && detail.getProduct().getId().equals(product.getId()) &&
                            detail.getBuy().getOriginDate().after(modified_with) && detail.getBuy().getOriginDate().before(modified_by))
                    .mapToInt(Details::getQuantity)
                    .sum();
            double revenue = product.getRevenue(modified_with, modified_by);
            double cost = product.getCostPrice(modified_with, modified_by);
            double difference = product.getDifferent(modified_with, modified_by);

            double profitShare = Math.round((((difference * 100) / allDifference) * 10.0) / 10.0);
            String group = "";
            if (profitShare  >= 10 && profitShare < 80) {
                group = "B";
                productsBCategory.add(product);
            } else if (profitShare < 10 && profitShare != 0) {
                group = "С";
                productsCCategory.add(product);
            } else if (profitShare >= 80){
                group = "A";
                productsACategory.add(product);
            }
            OnlineABCDTO onlineABCDTO = new OnlineABCDTO(
                    product.getId(), product.getName(), product.getCategory().getName(), group, profitShare,
                    quantity, cost, revenue, difference);
            onlineABCDTOS.add(onlineABCDTO);
        }
        String labelText = generateRecommendations(productsACategory, productsBCategory, productsCCategory);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("labelText", labelText);
        resultMap.put("onlineABCDTOS", onlineABCDTOS);
        return resultMap;
    }
    public boolean compareDate(String with, String by) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = sdf.parse(by);
        Date date2 = sdf.parse(with);
        return date2.after(date1);
    }
    public String generateRecommendations(List<Products> categoryA, List<Products> categoryB, List<Products> categoryC) {
        String description = "";

        if (!categoryA.isEmpty()) {
            description += "Рекомендуется активное управление запасами и уделение повышенного внимания товаров, находящихся в категории \"А\": " + printProductNames(categoryA) + "\n";
        } else {
            description += "В категории \"А\" товары отсутствуют\n";
        }

        if (!categoryB.isEmpty()) {
            description += "Рекомендуется стандартное управление запасами для товаров, находящихся в категории \"B\": " + printProductNames(categoryB) + "\n";
        } else {
            description += "В категории \"B\" товары отсутствуют\n";
        }

        if (!categoryC.isEmpty()) {
            description += "Рекомендуется снизить запасы следующих товаров, находящихся в категории \"C\": " + printProductNames(categoryC) + "\n";
        } else {
            description += "В категории \"С\" товары отсутствуют\n";
        }
        return description;
    }

    private String printProductNames(List<Products> products) {
        String text = "";
        for (Products product : products) {
            text += (product.getName() + "; ");
        }
        return text;
    }
}
