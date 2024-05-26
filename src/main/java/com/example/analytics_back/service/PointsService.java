package com.example.analytics_back.service;

import com.example.analytics_back.DTO.PointsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.Points;
import com.example.analytics_back.model.Regions;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.repo.PointsRepository;
import com.example.analytics_back.service.DTOConvectors.PointsDTOConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PointsService {
    private final PointsRepository pointsRepository;
    private final PointsDTOConverter pointsDTOConverter;

    private final UsersService usersService;
    private final RegionsService regionsService;

    public Points getPoint(Long pointId) {
        return pointsRepository.findById(pointId)
                .orElseThrow(() -> new CustomNotFoundException("Невозможно получить данные точки выдачи!"));
    }

    public List<PointsDTO> getPoints() {
        Users user = usersService.getUserInfo();
        List<Points> pointsList = user.getPoints();
        return pointsList.stream()
                .map(pointsDTOConverter::convertToDTO)
                .toList();
    }

    public PointsDTO pointAdd(PointsDTO pointsDTO) throws CustomException {
        Users user = usersService.getUserInfo();
        Regions region = regionsService.getRegion(pointsDTO.getRegionId());
        if (pointsRepository.existsByAddressAndRegionAndOwner(pointsDTO.getAddress(), region, user)) {
            throw new CustomException("В регионе " + region.getName() + " по адресу " + pointsDTO.getAddress() +
                    " уже есть пункт выдачи!");
        }
        Points point = new Points(pointsDTO.getAddress(), region, user);
        pointsRepository.save(point);
        return pointsDTOConverter.convertToDTO(point);
    }

    public PointsDTO pointEdit(PointsDTO pointsDTO) throws CustomException {
        Users user = usersService.getUserInfo();
        Points point = getPoint(pointsDTO.getId());
        Regions region = regionsService.getRegion(pointsDTO.getRegionId());
        if (pointsDTO.getAddress().matches(point.getAddress()) && pointsDTO.getRegionId() == point.getRegion().getId()) {
            throw new CustomException("Пукнт выдачи не нуждается в обновлнии!");
        }
        if (pointsRepository.existsByAddressAndRegionAndOwner(pointsDTO.getAddress(), region, user)) {
            throw new CustomException("В регионе " + region.getName() + " по адресу " + pointsDTO.getAddress() +
                    " уже есть пункт выдачи!");
        }
        Points updatedPoints = new Points(pointsDTO.getAddress(), region, point.getOwner());
        updatedPoints.setId(point.getId());
        pointsRepository.save(updatedPoints);
        return pointsDTOConverter.convertToDTO(updatedPoints);
    }

    public void pointDelete(Long pointId) {
        Points point = getPoint(pointId);
        pointsRepository.delete(point);
    }
}
