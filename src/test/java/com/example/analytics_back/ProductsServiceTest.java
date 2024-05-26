package com.example.analytics_back;

import com.example.analytics_back.DTO.ProductDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.*;
import com.example.analytics_back.repo.CategoriesRepository;
import com.example.analytics_back.repo.ProductsRepository;
import com.example.analytics_back.repo.UsersRepository;
import com.example.analytics_back.service.DTOConvectors.ProductDTOConverter;
import com.example.analytics_back.service.ProductsService;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class ProductsServiceTest {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private ProductsService productsService;
    @Autowired
    private ProductsRepository productsRepository;
    @Autowired
    private ProductDTOConverter productDTOConverter;

    @Test
    void testAddProduct() throws CustomException {
        // Given

        ProductDTO productDTO = new ProductDTO("название для теста", 56, 78L);
        ProductDTO waitProductDTO = productsService.productAdd(productDTO);

        // Create an expected ProductDTO
        ProductDTO expectedProductDTO = new ProductDTO("название для теста", 56, 78L);

        // Assert that the attributes of the ProductDTO objects are equal
        assertEquals(expectedProductDTO.getName(), waitProductDTO.getName());
        assertEquals(expectedProductDTO.getPrice(), waitProductDTO.getPrice());
        assertEquals(expectedProductDTO.getCategoryId(), waitProductDTO.getCategoryId());
    }

}