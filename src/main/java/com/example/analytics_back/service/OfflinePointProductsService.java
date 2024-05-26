package com.example.analytics_back.service;

import com.example.analytics_back.DTO.OfflinePointProductsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.*;
import com.example.analytics_back.repo.OfflinePointProductsRepository;
import com.example.analytics_back.service.DTOConvectors.OfflinePointProductsConvector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OfflinePointProductsService {

    private final OfflinePointProductsConvector offlinePointProductsConvector;
    private final OfflinePointProductsRepository offlinePointProductsRepository;
    private final OfflinePointsService offlinePointsService;
    private final ProductsService productsService;

    public OfflinePointProducts getOfflinePointProduct(Long offlinePointProductId) {
        return offlinePointProductsRepository.findById(offlinePointProductId)
                .orElseThrow(() -> new CustomNotFoundException("Невозможно получить данные товара в наличии!"));
    }

    public List<OfflinePointProductsDTO> getOfflinePointProducts(Long offlinePointId) {
        OfflinePoints offlinePoint = offlinePointsService.getOfflinePoint(offlinePointId);
        List<OfflinePointProducts> offlinePointProducts = offlinePoint.getOfflinePointProducts();
        return offlinePointProducts.stream()
                .map(offlinePointProductsConvector::convertToDTO)
                .toList();
    }
    public OfflinePointProductsDTO offlinePointProductsAdd(OfflinePointProductsDTO offlinePointProductsDTO,
                                              Long offlinePointId) throws CustomException {
        Products product = productsService.getProduct(offlinePointProductsDTO.getProductId());
        OfflinePoints offlinePoint = offlinePointsService.getOfflinePoint(offlinePointId);
        if (offlinePointProductsRepository.existsByProductAndOfflinePoints(product, offlinePoint)) {
            throw new CustomException("Данный товар уже добавлен в список наличия!");
        }
        OfflinePointProducts offlinePointProduct = new OfflinePointProducts(offlinePointProductsDTO.getQuantity(),
                product, offlinePoint);
        offlinePointProductsRepository.save(offlinePointProduct);
        return offlinePointProductsConvector.convertToDTO(offlinePointProduct);
    }
    public OfflinePointProductsDTO offlinePointProductsEdit(OfflinePointProductsDTO offlinePointProductsDTO)
            throws CustomException {
        OfflinePointProducts updatedOfflinePointProduct = getOfflinePointProduct(offlinePointProductsDTO.getId());
        Products product = productsService.getProduct(offlinePointProductsDTO.getProductId());
        if (offlinePointProductsDTO.getProductId() == updatedOfflinePointProduct.getProduct().getId()
                && offlinePointProductsDTO.getQuantity() == updatedOfflinePointProduct.getQuantity()) {
            throw new CustomException("Данный товар не нуждается в обновлении!");
        }
        updatedOfflinePointProduct.setQuantity(offlinePointProductsDTO.getQuantity());
        updatedOfflinePointProduct.setProduct(product);
        offlinePointProductsRepository.save(updatedOfflinePointProduct);
        return offlinePointProductsConvector.convertToDTO(updatedOfflinePointProduct);
    }
    public void offlinePointProductsDelete(Long offlinePointProductId) {
        OfflinePointProducts offlinePointProduct = getOfflinePointProduct(offlinePointProductId);
        offlinePointProductsRepository.delete(offlinePointProduct);
    }
}
