package com.example.analytics_back.service.onlineAnalytics;

import com.example.analytics_back.DTO.onlineAnalytics.OnlineGeneralDTO;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
@Slf4j
@RequiredArgsConstructor
public class OnlineGeneralService {
    private final UsersRepository usersRepository;
    private final ProductsRepository productsRepository;
    private final DetailsRepository detailsRepository;
    public List<OnlineGeneralDTO> general(Long userId) throws CustomException {
        if(!usersRepository.existsById(userId)){
            throw new CustomException("Пользователь не обнаружен, невозможно получить данные!");
        }
        Users users = usersRepository.findById(userId).orElseThrow();
        List<Products> products = productsRepository.findAllByOwner(users);
        List<OnlineGeneralDTO> onlineGeneralDTOList = new ArrayList<>();
        for (Products product : products) {
            int quantity = detailsRepository.findAll().stream()
                    .filter(detail -> detail.getProduct() != null && detail.getProduct().getId().equals(product.getId()))
                    .mapToInt(Details::getQuantity)
                    .sum();
            double revenue = product.getRevenue();
            double cost = product.getCostPrice();
            double difference = product.getDifferent();

            OnlineGeneralDTO onlineGeneralDTO = new OnlineGeneralDTO(
                    product.getId(), product.getName(), product.getCategory().getName(),
                    quantity, cost, revenue, difference);
            onlineGeneralDTOList.add(onlineGeneralDTO);
        }
        return onlineGeneralDTOList;
    }
    public List<OnlineGeneralDTO> generalFiltered(Long userId, String date_with, String date_by)
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
        List<OnlineGeneralDTO> onlineGeneralDTOList = new ArrayList<>();

        for (Products product : products) {
            int quantity = detailsRepository.findAll().stream()
                    .filter(detail -> detail.getProduct() != null && detail.getProduct().getId().equals(product.getId()) &&
                            detail.getBuy().getOriginDate().after(modified_with) && detail.getBuy().getOriginDate().before(modified_by))
                    .mapToInt(Details::getQuantity)
                    .sum();
            double revenue = product.getRevenue(modified_with, modified_by);
            double cost = product.getCostPrice(modified_with, modified_by);
            double difference = product.getDifferent(modified_with, modified_by);

            OnlineGeneralDTO onlineGeneralDTO = new OnlineGeneralDTO(
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
