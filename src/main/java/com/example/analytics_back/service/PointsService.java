package com.example.analytics_back.service;

import com.example.analytics_back.DTO.PointsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Points;
import com.example.analytics_back.model.Regions;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.repo.PointsRepository;
import com.example.analytics_back.repo.RegionsRepository;
import com.example.analytics_back.repo.UsersRepository;
import com.example.analytics_back.service.DTOConvectors.PointsDTOConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PointsService {

    private final UsersRepository usersRepository;
    private final RegionsRepository regionsRepository;
    private final PointsRepository pointsRepository;
    private final PointsDTOConverter pointsDTOConverter;

    public List<Points> points(Long userId) throws CustomException {
        Users user = usersRepository.findById(userId).orElseThrow();
        if (user == null) {
            throw new CustomException("Невозможно получить данные пунктов выдачи!");
        }
        return user.getPoints();
    }

    public PointsDTO pointAdd(PointsDTO pointsDTO, Long userId) throws CustomException {
        Users user = usersRepository.findById(userId).orElseThrow();
        if (user == null) {
            throw new CustomException("Вы не можете добавить данные для пункта выдачи!");
        }
        Regions region = regionsRepository.findById(pointsDTO.getRegionId()).orElseThrow();
        if (region == null) {
            throw new CustomException("Добавляемый регион для пункта выдачи не найден!");
        }
        Points point = new Points(pointsDTO.getAddress(), region, user);
        pointsRepository.save(point);
        return pointsDTOConverter.convertToDTO(point);
    }

    public PointsDTO pointEdit(PointsDTO pointsDTO) throws CustomException {
        Points point = pointsRepository.getReferenceById(pointsDTO.getId());
        if (point == null) {
            throw new CustomException("Изменяемый пункт выдачи не найден!");
        }
        Regions region = regionsRepository.findById(pointsDTO.getRegionId()).orElseThrow();
        if (region == null) {
            throw new CustomException("Добавляемый регион для пункта выдачи не найден!");
        }
        if (pointsDTO.getAddress().matches(point.getAddress()) && pointsDTO.getRegionId() == point.getRegion().getId()) {
            throw new CustomException("Пукнт выдачи не нуждается в обновлнии!");
        }

        Points updatedPoints = new Points(pointsDTO.getAddress(), region, point.getOwner());
        updatedPoints.setId(point.getId());
        pointsRepository.save(updatedPoints);
        return pointsDTOConverter.convertToDTO(updatedPoints);
    }

    public void pointDelete(Long pointId) throws CustomException {
        Points point = pointsRepository.getReferenceById(pointId);
        if (point == null) {
            throw new CustomException("Пункт выдачи не найден!");
        }
        pointsRepository.deleteById(pointId);
    }
}
