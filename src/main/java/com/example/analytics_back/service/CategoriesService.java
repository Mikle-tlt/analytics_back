package com.example.analytics_back.service;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.Categories;
import com.example.analytics_back.model.Points;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.repo.CategoriesRepository;
import com.example.analytics_back.repo.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoriesService {
    private final UsersService usersService;
    private final CategoriesRepository categoriesRepository;

    public Categories getCategory(Long categoryId) {
        return categoriesRepository.findById(categoryId)
                .orElseThrow(() -> new CustomNotFoundException("Невозможно получить данные категории!"));
    }
    public List<Categories> getCategories() {
        Users user = usersService.getUserInfo();
        return user.getCategories();
    }

    public Categories categoryAdd(String name) throws CustomException {
        Users user = usersService.getUserInfo();
        if (categoriesRepository.existsByNameAndOwner(name, user)) {
            throw new CustomException("Категория " + name + " уже существует в системе!");
        }
        Categories category = new Categories();
        category.setName(name);
        category.setOwner(user);
        return categoriesRepository.save(category);
    }
    public Categories categoryEdit(Categories category) throws CustomException {
        Users user = usersService.getUserInfo();
        Categories updatedCategory = getCategory(category.getId());
        if (!Objects.equals(category.getName(), updatedCategory.getName()) &&
                categoriesRepository.existsByNameAndOwner(category.getName(), user)) {
            throw new CustomException("Категория " + category.getName() + " уже существует в системе!");
        }
        updatedCategory.setName(category.getName());
        return categoriesRepository.save(updatedCategory);
    }

    public void categoryDelete(Long categoryId) {
        Categories category = getCategory(categoryId);
        categoriesRepository.delete(category);
    }
}
