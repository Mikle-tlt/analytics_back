package com.example.analytics_back.service;

import com.example.analytics_back.DTO.ProductDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.*;
import com.example.analytics_back.repo.ProductsRepository;
import com.example.analytics_back.service.DTOConvectors.ProductDTOConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductsService {

    private final ProductsRepository productsRepository;
    private final ProductDTOConverter productDTOConverter;
    private final UsersService usersService;
    private final CategoriesService categoriesService;

    public Products getProduct(Long productId) {
        return productsRepository.findById(productId)
                .orElseThrow(() -> new CustomNotFoundException("Невозможно получить данные товара!"));
    }
    public List<ProductDTO> getProducts() {
        Users user = usersService.getUserInfo();
        List<Products> productsList = user.getProducts();
        return productsList.stream()
                .map(productDTOConverter::convertToDTO)
                .toList();
    }
    public ProductDTO productAdd(ProductDTO productDTO) throws CustomException {
        Users user = usersService.getUserInfo();
        Categories category = categoriesService.getCategory(productDTO.getCategoryId());
        if (getByNameAndCategoryAndOwner(productDTO.getName(), category, user) != null) {
            throw new CustomException("Товар с данным названием и категорией уже существует!");
        }
        Products product = new Products(user, category, productDTO.getName(), productDTO.getPrice());
        productsRepository.save(product);
        return productDTOConverter.convertToDTO(product);
    }
    public ProductDTO productEdit(ProductDTO productDTO) throws CustomException {
        Products product = getProduct(productDTO.getId());
        Categories category = categoriesService.getCategory(productDTO.getCategoryId());
        if (product.getName().matches(productDTO.getName()) &&
                product.getCategory().getId().equals(productDTO.getCategoryId())) {
            product.setPrice(productDTO.getPrice());
            productsRepository.save(product);
            return productDTOConverter.convertToDTO(product);
        }
        if (productsRepository.existsByNameAndCategoryAndOwner(productDTO.getName(), category, product.getOwner())) {
            throw new CustomException("Товар с данным названием и категорией уже существует!");
        }
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setCategory(category);
        productsRepository.save(product);
        return productDTOConverter.convertToDTO(product);
    }

    public void productDelete(Long productId) {
        Products product = getProduct(productId);
        productsRepository.delete(product);
    }
    public Products getByNameAndCategoryAndOwner(String productName, Categories category, Users productOwner) {
        return productsRepository.findByNameAndCategoryAndOwner(productName, category, productOwner);
    }
}
