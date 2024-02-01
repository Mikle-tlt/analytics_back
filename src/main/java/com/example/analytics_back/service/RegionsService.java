package com.example.analytics_back.service;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Categories;
import com.example.analytics_back.model.Regions;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.repo.CategoriesRepository;
import com.example.analytics_back.repo.RegionsRepository;
import com.example.analytics_back.repo.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegionsService {
    private final UsersRepository usersRepository;
    private final RegionsRepository regionsRepository;

    public List<Regions> regions(Long userId) throws CustomException {
        Users user = usersRepository.findById(userId).orElseThrow();
        if (user == null) {
            throw new CustomException("Невозможно получить данные регионов!");
        }
        return user.getRegions();
    }

    public Regions regionAdd(String name, Long userId) throws CustomException {
        Users user = usersRepository.getReferenceById(userId);
        if (user == null) {
            throw new CustomException("Вы не можете добавить данные для региона!");
        }
        Regions regions = new Regions();
        regions.setName(name);
        regions.setOwner(user);
        return regionsRepository.save(regions);
    }
    public Regions regionEdit(String name, Long categoryId) throws CustomException {
        Regions region = regionsRepository.findById(categoryId).orElseThrow();
        if (region == null) {
            throw new CustomException("Регион не найден в системе!");
        }
        region.setName(name);
        regionsRepository.save(region);
        return region;
    }
    public void regionDelete(Long categoryId) throws CustomException {
        Regions region = regionsRepository.findById(categoryId).orElseThrow();
        if (region == null) {
            throw new CustomException("Регион не найден в системе!");
        }
        regionsRepository.deleteById(categoryId);
    }
}
