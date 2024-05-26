package com.example.analytics_back.service.offlineAnalytics;

import com.example.analytics_back.DTO.analytics.GeneralDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.*;
import com.example.analytics_back.repo.OfflineDetailsRepository;
import com.example.analytics_back.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OfflineGeneralService {

    private final UsersService usersService;
    private final OfflineDetailsRepository offlineDetailsRepository;

    public List<GeneralDTO> general() {
        Users users = usersService.getUserInfo();
        List<Products> products = users.getProducts();
        List<GeneralDTO> onlineGeneralDTOList = new ArrayList<>();

        for (Products product : products) {
            int quantity = offlineDetailsRepository.findAll().stream()
                    .filter(detail -> {
                        OfflinePointProducts offlinePointProducts = detail.getOfflinePointProducts();
                        return offlinePointProducts != null &&
                                offlinePointProducts.getProduct() != null &&
                                offlinePointProducts.getProduct().getId().equals(product.getId());
                    })
                    .mapToInt(OfflineDetails::getQuantity)
                    .sum();
            double revenue = product.getRevenueOffline();
            double cost = product.getCostPriceOffline();
            double difference = product.getDifferentOffline();

            GeneralDTO onlineGeneralDTO = new GeneralDTO(
                    product.getId(), product.getName(), product.getCategory().getName(),
                    quantity, cost, revenue, difference);
            onlineGeneralDTOList.add(onlineGeneralDTO);
        }
        return onlineGeneralDTOList;
    }

    public List<GeneralDTO> generalFiltered(String date_with, String date_by) throws ParseException, CustomException {
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

        List<Products> products = user.getProducts();
        List<GeneralDTO> onlineGeneralDTOList = new ArrayList<>();

        for (Products product : products) {
            int quantity = offlineDetailsRepository.findAll().stream()
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
            double revenue = product.getRevenueOffline(modified_with, modified_by);
            double cost = product.getCostPriceOffline(modified_with, modified_by);
            double difference = product.getDifferentOffline(modified_with, modified_by);

            GeneralDTO onlineGeneralDTO = new GeneralDTO(
                    product.getId(), product.getName(), product.getCategory().getName(),
                    quantity, cost, revenue, difference);
            onlineGeneralDTOList.add(onlineGeneralDTO);
        }
        return onlineGeneralDTOList;

    }

    public boolean compareDate(String with, String by) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = sdf.parse(by);
        Date date2 = sdf.parse(with);
        return date2.after(date1);
    }
}
