package com.example.analytics_back.service;

import com.example.analytics_back.DTO.BuysDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.*;
import com.example.analytics_back.repo.BuysRepository;
import com.example.analytics_back.repo.ClientsRepository;
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
    private final BuysDTOConverter buysDTOConverter;
    private final ClientsService clientsService;
    private final PointsService pointsService;
    private final ClientsRepository clientsRepository;

    public Buys getBuy(Long buyId) {
        return buysRepository.findById(buyId)
                .orElseThrow(() -> new CustomNotFoundException("Невозможно получить данные покупки!"));
    }
    public BuysDTO getBuyDTO(Long buyId) {
        Buys buys = getBuy(buyId);
        return new BuysDTO(buyId, buys.getDate(), buys.getPoints().getId(),
                buys.getPoints().getAddress(), buys.getCostPrice(), buys.getRevenue(), buys.getDifferent());
    }
    public List<BuysDTO> getOnlineBuys(Long clientId) {
        Clients client = clientsService.getClient(clientId);
        List<Buys> buys = client.getBuys();
        return buys.stream()
                .map(buysDTOConverter::convertToDTO)
                .toList();
    }
    public BuysDTO onlineBuyAdd(Long clientId, BuysDTO buysDTO) throws CustomException, ParseException {
        Clients client = clientsService.getClient(clientId);
        Points points = pointsService.getPoint(buysDTO.getPointId());
        Buys buys = new Buys(buysDTO.getDate(), points, client);
        if (compareDate(buys.getToday(), buysDTO.getDate())) {
            throw new CustomException("Запрещено создавать покупку на будущую дату!");
        }
        if (buysRepository.existsByDateAndPointsAndClient(new SimpleDateFormat("yyyy-MM-dd")
                .parse(buysDTO.getDate()), points, client)) {
            throw new CustomException("Покупка с указанной датой и местом выдачи уже была добавлена ранее!");
        }
        if (client.getDate() == null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            client.setDate(dateFormat.parse(buysDTO.getDate()));
            clientsRepository.save(client);
        }
        buysRepository.save(buys);
        return buysDTOConverter.convertToDTO(buys);
    }

    public BuysDTO onlineBuyEdit(BuysDTO buysDTO) throws CustomException, ParseException {
        Points points = pointsService.getPoint(buysDTO.getPointId());
        Buys buys = getBuy(buysDTO.getId());
        Clients client = clientsService.getClient(buys.getClient().getId());
        if (buys.getDate().matches(buysDTO.getDate()) && buys.getPoints().getId() == buysDTO.getPointId()) {
            throw new CustomException("Данные о покупке не нуждаются в обновлении");
        }
        if (compareDate(buys.getToday(), buysDTO.getDate())) {
            throw new CustomException("Запрещено создавать покупку на будущую дату!");
        }
        if (buysRepository.existsByDateAndPointsAndClient(new SimpleDateFormat("yyyy-MM-dd")
                .parse(buysDTO.getDate()), points, client)) {
            throw new CustomException("Покупка с указанной датой и местом выдачи уже была добавлена ранее!");
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
        Buys buys = getBuy(buyId);
        buysRepository.delete(buys);
    }
}
