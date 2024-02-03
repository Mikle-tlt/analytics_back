package com.example.analytics_back.service;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.OfflineBuys;
import com.example.analytics_back.model.OfflinePoints;
import com.example.analytics_back.repo.OfflineBuysRepository;
import com.example.analytics_back.repo.OfflinePointsRepository;
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
    private final OfflinePointsRepository offlinePointsRepository;
    private final OfflineBuysRepository offlineBuysRepository;

    public List<OfflineBuys> offlineBuys(Long offlinePointId) throws CustomException {
        if (!offlinePointsRepository.existsById(offlinePointId)) {
            throw new CustomException("Невозможно получить данные продаж!");
        }
        OfflinePoints offlinePoints = offlinePointsRepository.getReferenceById(offlinePointId);
        return offlinePoints.getOfflineBuys()
                .stream()
                .sorted(Comparator.comparing(OfflineBuys::getOriginDate))
                .collect(Collectors.toList());
    }
    public OfflineBuys offlineBuysAdd(String date, Long offlinePointId) throws CustomException, ParseException {
        if (!offlinePointsRepository.existsById(offlinePointId)) {
            throw new CustomException("Указанная оффлайн точка не найдена!");
        }
        OfflinePoints offlinePoint = offlinePointsRepository.getReferenceById(offlinePointId);
        OfflineBuys offlineBuy = new OfflineBuys(date, offlinePoint);
        if (compareDate(offlineBuy.getToday(), date)) {
            throw new CustomException("Запрещено создавать продажу на будущую дату!");
        }
        return offlineBuysRepository.save(offlineBuy);
    }

    public boolean compareDate(String today, String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = sdf.parse(today);
        Date date2 = sdf.parse(date);
        return date2.after(date1);
    }

    public OfflineBuys offlineBuysEdit(String date, Long offlineBuysId) throws CustomException, ParseException {
        if (!offlineBuysRepository.existsById(offlineBuysId)) {
            throw new CustomException("Указанная продажа не найдена!");
        }
        OfflineBuys offlineBuy = offlineBuysRepository.findById(offlineBuysId).orElseThrow();
        OfflineBuys updatedOfflineBuy = new OfflineBuys(date, offlineBuy.getOfflinePoints());
        updatedOfflineBuy.setId(offlineBuysId);
        return offlineBuysRepository.save(updatedOfflineBuy);
    }

    public void offlineBuysDelete(Long offlineBuyId) throws CustomException {
        if (!offlineBuysRepository.existsById(offlineBuyId)) {
            throw new CustomException("Данная покупка отсутствует в системе!");
        }
        offlineBuysRepository.deleteById(offlineBuyId);
    }
}
