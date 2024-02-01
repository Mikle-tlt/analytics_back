package com.example.analytics_back.service;

import com.example.analytics_back.DTO.ProductDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.*;
import com.example.analytics_back.repo.CategoriesRepository;
import com.example.analytics_back.repo.ProductsRepository;
import com.example.analytics_back.repo.UsersRepository;
import com.example.analytics_back.service.DTOConvectors.ProductDTOConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductsService {

    private final UsersRepository usersRepository;
    private final ProductsRepository productsRepository;
    private final CategoriesRepository categoriesRepository;
    private final ProductDTOConverter productDTOConverter;

    public List<Products> products(Long userId) throws CustomException {
        Users user = usersRepository.findById(userId).orElseThrow();
        if (user == null) {
            throw new CustomException("Невозможно получить данные продуктов!");
        }
        return user.getProducts();
    }
    public ProductDTO productAdd(ProductDTO productDTO, Long userId) throws CustomException {
        Users user = usersRepository.getReferenceById(userId);
        if (user == null) {
            throw new CustomException("Вы не можете добавить данные для товара!");
        }
        Categories category = categoriesRepository.findById(productDTO.getCategoryId()).orElseThrow();
        if (category == null) {
            throw new CustomException("Добавляемая категория товара не найдена!");
        }
        if (productsRepository.findByNameAndCategoryAndOwner(productDTO.getName(), category, user) != null) {
            throw new CustomException("Товар с данным названием и категорией уже существует!");
        }
        Products product = new Products(user, category, productDTO.getName(), productDTO.getPrice());
        productsRepository.save(product);
        return productDTOConverter.convertToDTO(product);
    }
    public ProductDTO productEdit(ProductDTO productDTO) throws CustomException {
        Products product = productsRepository.getReferenceById(productDTO.getId());
        if (product == null) {
            throw new CustomException("Изменяемый товар не найден!");
        }
        Categories category = categoriesRepository.findById(productDTO.getCategoryId()).orElseThrow();
        if (category == null) {
            throw new CustomException("Добавляемая категория товара не найдена!");
        }
        if (!product.getName().matches(productDTO.getName()) &&
                productsRepository.findByNameAndCategoryAndOwner(productDTO.getName(), category, product.getOwner()) != null) {
            throw new CustomException("Товар с данным названием и категорией уже существует!");
        }
        Products updatedProduct = new Products(product.getOwner(), category, productDTO.getName(), productDTO.getPrice());
        updatedProduct.setId(product.getId());
        productsRepository.save(updatedProduct);
        return productDTOConverter.convertToDTO(updatedProduct);
    }

    public void productDelete(Long productId) throws CustomException {
        Products product = productsRepository.findById(productId).orElseThrow();
        if (product == null) {
            throw new CustomException("Продукт не найден в системе!");
        }
        productsRepository.deleteById(productId);
    }
}
