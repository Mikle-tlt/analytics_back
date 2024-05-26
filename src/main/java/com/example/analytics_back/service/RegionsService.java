package com.example.analytics_back.service;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.Regions;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.repo.RegionsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegionsService {
    private final RegionsRepository regionsRepository;
    private final UsersService usersService;

    public Regions getRegion(Long regionId) {
        return regionsRepository.findById(regionId)
                .orElseThrow(() -> new CustomNotFoundException("Невозможно получить данные региона!"));
    }

    public List<Regions> getRegions() throws CustomException {
        Users user = usersService.getUserInfo();
        return user.getRegions();
    }

    public Regions regionAdd(String name) throws CustomException {
        Users user = usersService.getUserInfo();
        if (regionsRepository.existsByNameAndOwner(name, user)) {
            throw new CustomException("Регион \"" + name + "\" уже существует в системе!");
        }
        Regions regions = new Regions();
        regions.setName(name);
        regions.setOwner(user);
        return regionsRepository.save(regions);
    }
    public Regions regionEdit(Regions region) throws CustomException {
        Users user = usersService.getUserInfo();
        Regions updatedRegion = getRegion(region.getId());
        if (!Objects.equals(region.getName(), updatedRegion.getName()) &&
                regionsRepository.existsByNameAndOwner(region.getName(), user)) {
            throw new CustomException("Регион \"" + region.getName() + "\" уже существует в системе!");
        }
        updatedRegion.setName(region.getName());
        regionsRepository.save(updatedRegion);
        return updatedRegion;
    }
    public void regionDelete(Long regionId) throws CustomException {
        Regions region = getRegion(regionId);
        regionsRepository.delete(region);
    }
}
