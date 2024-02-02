package com.example.analytics_back.service;

import com.example.analytics_back.DTO.OfflinePointsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.OfflinePoints;
import com.example.analytics_back.model.Regions;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.repo.OfflinePointsRepository;
import com.example.analytics_back.repo.RegionsRepository;
import com.example.analytics_back.repo.UsersRepository;
import com.example.analytics_back.service.DTOConvectors.OfflinePointsConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OfflinePointsService {
    private final UsersRepository usersRepository;
    private final OfflinePointsRepository offlinePointsRepository;
    private final RegionsRepository regionsRepository;
    private final OfflinePointsConverter offlinePointsConverter;

    public List<OfflinePoints> offlinePoints(Long userId) throws CustomException {
        Users user = usersRepository.findById(userId).orElseThrow();
        if (user == null) {
            throw new CustomException("Невозможно получить данные пунктов выдачи!");
        }
        return user.getOfflinePoints();
    }

    public OfflinePointsDTO offlinePointsAdd(OfflinePointsDTO offlinePointsDTO, Long userId) throws CustomException {
        Users user = usersRepository.findById(userId).orElseThrow();
        if (user == null) {
            throw new CustomException("Вы не можете добавить данные для пункта выдачи!");
        }
        Regions region = regionsRepository.findById(offlinePointsDTO.getRegionId()).orElseThrow();
        if (region == null) {
            throw new CustomException("Добавляемый регион для пункта выдачи не найден!");
        }
        OfflinePoints offlinePoints = new OfflinePoints(offlinePointsDTO.getName(), offlinePointsDTO.getAddress(), user, region);
        offlinePointsRepository.save(offlinePoints);
        return offlinePointsConverter.convertToDTO(offlinePoints);
    }

    public OfflinePointsDTO offlinePointsEdit(OfflinePointsDTO offlinePointsDTO) throws CustomException {
        OfflinePoints offlinePoints = offlinePointsRepository.getReferenceById(offlinePointsDTO.getId());
        if (offlinePoints == null) {
            throw new CustomException("Изменяемый пункт выдачи не найден!");
        }
        Regions region = regionsRepository.findById(offlinePointsDTO.getRegionId()).orElseThrow();
        if (region == null) {
            throw new CustomException("Добавляемый регион для пункта выдачи не найден!");
        }
        if (offlinePointsDTO.getAddress().matches(offlinePoints.getAddress()) && offlinePointsDTO.getRegionId() == offlinePoints.getRegion().getId() &&
                offlinePointsDTO.getName().matches(offlinePoints.getName())) {
            throw new CustomException("Пукнт выдачи не нуждается в обновлнии!");
        }
        OfflinePoints updatedOfflinePoint = new OfflinePoints(offlinePointsDTO.getName(), offlinePointsDTO.getAddress(), offlinePoints.getOwner(), region);
        updatedOfflinePoint.setId(offlinePointsDTO.getId());
        offlinePointsRepository.save(updatedOfflinePoint);
        return offlinePointsConverter.convertToDTO(updatedOfflinePoint);
    }

    public void offlinePointsDelete(Long offlinePointId) throws CustomException {
        if (!offlinePointsRepository.existsById(offlinePointId)) {
            throw new CustomException("Пункт выдачи не найден!");
        }
        offlinePointsRepository.deleteById(offlinePointId);
    }
}
