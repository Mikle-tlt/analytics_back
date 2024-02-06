package com.example.analytics_back.service;

import com.example.analytics_back.DTO.BuysDTO;
import com.example.analytics_back.DTO.OfflineDetailsDTO;
import com.example.analytics_back.DTO.ProductDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.*;
import com.example.analytics_back.repo.BuysRepository;
import com.example.analytics_back.repo.ClientsRepository;
import com.example.analytics_back.repo.PointsRepository;
import com.example.analytics_back.service.DTOConvectors.BuysDTOConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BuysService {
    private final BuysRepository buysRepository;
    private final ClientsRepository clientsRepository;
    private final PointsRepository pointsRepository;
    private final BuysDTOConverter buysDTOConverter;

    public List<Buys> onlineBuys(Long clientId) throws CustomException {
        if (!clientsRepository.existsById(clientId)) {
            throw new CustomException("Указанная клиент не найден!");
        }
        Clients client = clientsRepository.findById(clientId).orElseThrow();
        return client.getBuys();
    }

    public BuysDTO getBuy(Long buyId) throws CustomException {
        if (!buysRepository.existsById(buyId)) {
            throw new CustomException("Указанная покупка не найдена!");
        }
        Buys buys = buysRepository.getReferenceById(buyId);
        return new BuysDTO(buyId, buys.getDate(), buys.getPoints().getId(),
                buys.getPoints().getAddress(), buys.getCostPrice(), buys.getRevenue(), buys.getDifferent());
    }

    public BuysDTO onlineBuyAdd(Long clientId, BuysDTO buysDTO) throws CustomException, ParseException {
        if (!clientsRepository.existsById(clientId)) {
            throw new CustomException("Указанный клиент не найден!");
        }
        if (!pointsRepository.existsById(buysDTO.getPointId())) {
            throw new CustomException("Указанный пункт выдачи не найден!");
        }
        Clients client = clientsRepository.findById(clientId).orElseThrow();
        Points points = pointsRepository.getReferenceById(buysDTO.getPointId());
        Buys buys = new Buys(buysDTO.getDate(), points, client);
        if (compareDate(buys.getToday(), buysDTO.getDate())) {
            throw new CustomException("Запрещено создавать покупку на будущую дату!");
        }
        if (buysRepository.existsByDateAndPointsAndClient(new SimpleDateFormat("yyyy-MM-dd")
                .parse(buysDTO.getDate()), points, client)) {
            throw new CustomException("Покупка с указанной датой и пунктом выдачи уже существует!");
        }
        buysRepository.save(buys);
        return buysDTOConverter.convertToDTO(buys);
    }

    public BuysDTO onlineBuyEdit(BuysDTO buysDTO) throws CustomException, ParseException {
        if (!buysRepository.existsById(buysDTO.getId())) {
            throw new CustomException("Указанная покупка не найдена!");
        }
        if (!pointsRepository.existsById(buysDTO.getPointId())) {
            throw new CustomException("Указанный пункт выдачи не найден!");
        }
        Points points = pointsRepository.getReferenceById(buysDTO.getPointId());
        Buys buys = buysRepository.getReferenceById(buysDTO.getId());
        if (buys.getDate().matches(buysDTO.getDate()) && buys.getPoints().getId() == buysDTO.getPointId()) {
            throw new CustomException("Данные о покупке не нуждаются в обновлении");
        }
        if (compareDate(buys.getToday(), buysDTO.getDate())) {
            throw new CustomException("Запрещено создавать покупку на будущую дату!");
        }
        Buys updatedBuys = new Buys(buysDTO.getDate(), points, buys.getClient());
        updatedBuys.setId(buysDTO.getId());
        buysRepository.save(updatedBuys);
        return buysDTOConverter.convertToDTO(updatedBuys);
    }

    public boolean compareDate(String today, String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = sdf.parse(today);
        Date date2 = sdf.parse(date);
        return date2.after(date1);
    }

    public void onlineBuyDelete(Long buyId) throws CustomException {
        if (!buysRepository.existsById(buyId)) {
            throw new CustomException("Указанная покупка не найдена!");
        }
        buysRepository.deleteById(buyId);
    }
}
