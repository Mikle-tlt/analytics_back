package com.example.analytics_back.service;

import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.Categories;
import com.example.analytics_back.model.Users;
import com.example.analytics_back.repo.CategoriesRepository;
import com.example.analytics_back.repo.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoriesService {
    private final UsersRepository usersRepository;
    private final CategoriesRepository categoriesRepository;
    public List<Categories> categories(Long userId) throws CustomException {
        Users user = usersRepository.findById(userId).orElseThrow();
        if (user == null) {
            throw new CustomException("Невозможно получить данные категорий!");
        }
        return user.getCategories();
    }

    public Categories categoryAdd(String name, Long userId) throws CustomException {
        Users user = usersRepository.getReferenceById(userId);
        if (user == null) {
            throw new CustomException("Вы не можете добавить данные для категории!");
        }
        Categories category = new Categories();
        category.setName(name);
        category.setOwner(user);
        return categoriesRepository.save(category);
    }
    public Categories categoryEdit(String name,Long categoryId) throws CustomException {
        Categories category = categoriesRepository.findById(categoryId).orElseThrow();
        if (category == null) {
            throw new CustomException("Категория не найдена в системе!");
        }
        category.setName(name);
        categoriesRepository.save(category);
        return category;
    }

    public void categoryDelete(Long categoryId) throws CustomException {
        Categories category = categoriesRepository.findById(categoryId).orElseThrow();
        if (category == null) {
            throw new CustomException("Категория не найдена в системе!");
        }
        categoriesRepository.deleteById(categoryId);
    }

}
