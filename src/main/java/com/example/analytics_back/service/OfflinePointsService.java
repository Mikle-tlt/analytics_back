package com.example.analytics_back.service;

import com.example.analytics_back.DTO.OfflinePointsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.OfflinePoints;
import com.example.analytics_back.model.Regions;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.repo.OfflinePointsRepository;
import com.example.analytics_back.service.DTOConvectors.OfflinePointsConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OfflinePointsService {
    private final OfflinePointsRepository offlinePointsRepository;
    private final OfflinePointsConverter offlinePointsConverter;
    private final UsersService usersService;
    private final RegionsService regionsService;

    public OfflinePoints getOfflinePoint(Long offlinePointId) {
        return offlinePointsRepository.findById(offlinePointId)
                .orElseThrow(() -> new CustomNotFoundException("Невозможно получить данные оффлайн точки!"));
    }
    public OfflinePointsDTO getOfflinePointDTO(Long offlinePointId) {
        OfflinePoints offlinePoint = getOfflinePoint(offlinePointId);
        return new OfflinePointsDTO(
                offlinePoint.getId(), offlinePoint.getAddress(),
                offlinePoint.getName(), offlinePoint.getRegion().getName(),
                offlinePoint.getCostPrice(), offlinePoint.getRevenue(), offlinePoint.getDifferent());
    }
    public List<OfflinePointsDTO> getOfflinePoints() {
        Users user = usersService.getUserInfo();
        List<OfflinePoints> offlinePointsList = user.getOfflinePoints();
        return offlinePointsList.stream()
                .map(offlinePointsConverter::convertToDTO)
                .toList();
    }

    public OfflinePointsDTO offlinePointAdd(OfflinePointsDTO offlinePointsDTO) throws CustomException {
        Users user = usersService.getUserInfo();
        Regions region = regionsService.getRegion(offlinePointsDTO.getRegionId());
        if (offlinePointsRepository.existsByAddressAndRegionAndOwner(offlinePointsDTO.getAddress(), region, user)) {
            throw new CustomException("Оффлайн точка в регионе " + region.getName() + " по адресу " +
                    offlinePointsDTO.getAddress() + " уже существует!");
        }
        OfflinePoints offlinePoints = new OfflinePoints(offlinePointsDTO.getName(),
                offlinePointsDTO.getAddress(), user, region);
        offlinePointsRepository.save(offlinePoints);
        return offlinePointsConverter.convertToDTO(offlinePoints);
    }

    public OfflinePointsDTO offlinePointEdit(OfflinePointsDTO offlinePointsDTO) throws CustomException {
        Users user = usersService.getUserInfo();
        OfflinePoints updatedOfflinePoint = getOfflinePoint(offlinePointsDTO.getId());
        Regions region = regionsService.getRegion(offlinePointsDTO.getRegionId());
        if (updatedOfflinePoint.getRegion().getId().equals(offlinePointsDTO.getRegionId()) &&
        updatedOfflinePoint.getAddress().matches(offlinePointsDTO.getAddress())) {
            updatedOfflinePoint.setName(offlinePointsDTO.getName());
            offlinePointsRepository.save(updatedOfflinePoint);
            return offlinePointsConverter.convertToDTO(updatedOfflinePoint);
        }
        if (offlinePointsRepository.existsByAddressAndRegionAndOwner(offlinePointsDTO.getAddress(), region, user)) {
            throw new CustomException("Оффлайн точка в регионе " + region.getName() + " по адресу " +
                    offlinePointsDTO.getAddress() + " уже существует!");
        }
        updatedOfflinePoint.setName(offlinePointsDTO.getName());
        updatedOfflinePoint.setAddress(offlinePointsDTO.getAddress());
        updatedOfflinePoint.setRegion(region);
        offlinePointsRepository.save(updatedOfflinePoint);
        return offlinePointsConverter.convertToDTO(updatedOfflinePoint);
    }

    public void offlinePointsDelete(Long offlinePointId) {
        OfflinePoints offlinePoint = getOfflinePoint(offlinePointId);
        offlinePointsRepository.delete(offlinePoint);
    }
}
