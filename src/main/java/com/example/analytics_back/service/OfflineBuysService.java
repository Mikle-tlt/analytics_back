package com.example.analytics_back.service;

import com.example.analytics_back.DTO.OfflineBuysDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.OfflineBuys;
import com.example.analytics_back.model.OfflinePoints;
import com.example.analytics_back.repo.OfflineBuysRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Service
@Slf4j
@RequiredArgsConstructor
public class OfflineBuysService {
    private final OfflineBuysRepository offlineBuysRepository;
    private final OfflinePointsService offlinePointsService;

    public OfflineBuys getOfflineBuy(Long offlineBuyId) {
        return offlineBuysRepository.findById(offlineBuyId)
                .orElseThrow(() -> new CustomNotFoundException("Невозможно получить данные оффлайн продажи!"));
    }

    public OfflineBuysDTO getOfflineBuyDTO(Long offlineBuyId) {
        OfflineBuys offlineBuys = getOfflineBuy(offlineBuyId);
        return new OfflineBuysDTO(
                offlineBuys.getId(), offlineBuys.getDate(),
                offlineBuys.getCostPrice(), offlineBuys.getRevenue(), offlineBuys.getDifferent());
    }

    public List<OfflineBuys> getOfflineBuys(Long offlinePointId) {
        OfflinePoints offlinePoint = offlinePointsService.getOfflinePoint(offlinePointId);
        return offlinePoint.getOfflineBuys()
                .stream()
                .sorted(Comparator.comparing(OfflineBuys::getOriginDate))
                .collect(Collectors.toList());
    }
    public OfflineBuys offlineBuyAdd(String date, Long offlinePointId) throws CustomException, ParseException {
        OfflinePoints offlinePoint = offlinePointsService.getOfflinePoint(offlinePointId);
        OfflineBuys offlineBuy = new OfflineBuys(date, offlinePoint);
        if (compareDate(offlineBuy.getToday(), date)) {
            throw new CustomException("Запрещено создавать продажу на будущую дату!");
        }
        if (offlineBuysRepository.existsByDateAndOfflinePoints(new SimpleDateFormat("yyyy-MM-dd")
                .parse(date), offlinePoint)) {
            throw new CustomException("Продажа с указанной датой и оффлайн точкой уже была добавлена ранее!");
        }
        return offlineBuysRepository.save(offlineBuy);
    }

    public OfflineBuys offlineBuysEdit(OfflineBuys offlineBuy) throws CustomException, ParseException {
        OfflineBuys updatedOfflineBuy = getOfflineBuy(offlineBuy.getId());
        if (compareDate(updatedOfflineBuy.getToday(), offlineBuy.getDate())) {
            throw new CustomException("Запрещено создавать продажу на будущую дату!");
        }
        if (updatedOfflineBuy.getDate().matches(offlineBuy.getDate())) {
            throw new CustomException("Данные о покупке не нуждаются в обновлении");
        }
        if (offlineBuysRepository.existsByDateAndOfflinePoints(new SimpleDateFormat("yyyy-MM-dd")
                .parse(offlineBuy.getDate()), updatedOfflineBuy.getOfflinePoints())) {
            throw new CustomException("Продажа с указанной датой уже была добавлена ранее!");
        }
        updatedOfflineBuy.setDate(offlineBuy.getDate());
        return offlineBuysRepository.save(updatedOfflineBuy);
    }

    public void offlineBuysDelete(Long offlineBuyId) {
        OfflineBuys updatedOfflineBuy = getOfflineBuy(offlineBuyId);
        offlineBuysRepository.delete(updatedOfflineBuy);
    }
    public boolean compareDate(String today, String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = sdf.parse(today);
        Date date2 = sdf.parse(date);
        return date2.after(date1);
    }
}
