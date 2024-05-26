package com.example.analytics_back.service.totalAnalytics;

import com.example.analytics_back.DTO.analytics.GeneralDTO;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TotalGeneralService {
    private final UsersService usersService;
    private final ProductsRepository productsRepository;
    private final DetailsRepository detailsRepository;
    private final OfflineDetailsRepository offlineDetailsRepository;
    public List<GeneralDTO> general() throws CustomException {
        Users user = usersService.getUserInfo();
        List<Products> products = productsRepository.findAllByOwner(user);
        List<GeneralDTO> onlineGeneralDTOList = new ArrayList<>();
        for (Products product : products) {
            int onlineQuantity = detailsRepository.findAll().stream()
                    .filter(detail -> detail.getProduct() != null && detail.getProduct().getId().equals(product.getId()))
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

            GeneralDTO onlineGeneralDTO = new GeneralDTO(
                    product.getId(), product.getName(), product.getCategory().getName(),
                    totalQuantity, totalCost, totalRevenue, totalDifference);
            onlineGeneralDTOList.add(onlineGeneralDTO);
        }
        return onlineGeneralDTOList;
    }
    public List<GeneralDTO> generalFiltered(String date_with, String date_by)
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
        List<GeneralDTO> onlineGeneralDTOList = new ArrayList<>();

        for (Products product : products) {
            int onlineQuantity = detailsRepository.findAll().stream()
                    .filter(detail -> detail.getProduct() != null && detail.getProduct().getId().equals(product.getId()) &&
                            detail.getBuy().getOriginDate().after(modified_with) && detail.getBuy().getOriginDate().before(modified_by))
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

            GeneralDTO onlineGeneralDTO = new GeneralDTO(
                    product.getId(), product.getName(), product.getCategory().getName(),
                    totalQuantity, totalRevenue, totalCost, totalDifference);
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
