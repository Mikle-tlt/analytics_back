package com.example.analytics_back.service;

import com.example.analytics_back.DTO.OfflinePointProductsDTO;
import com.example.analytics_back.DTO.ProductDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.*;
import com.example.analytics_back.repo.OfflinePointProductsRepository;
import com.example.analytics_back.repo.OfflinePointsRepository;
import com.example.analytics_back.repo.ProductsRepository;
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
    private final OfflinePointsRepository offlinePointsRepository;
    private final ProductsRepository productsRepository;

    public List<OfflinePointProducts> offlinePointProducts(Long offlinePointId) throws CustomException {
        if (!offlinePointsRepository.existsById(offlinePointId)) {
            throw new CustomException("Невозможно получить данные продуктов!");
        }
        OfflinePoints offlinePoints = offlinePointsRepository.getReferenceById(offlinePointId);
        return offlinePoints.getOfflinePointProducts();
    }
    public OfflinePointProductsDTO offlinePointProductsAdd(OfflinePointProductsDTO offlinePointProductsDTO,
                                              Long offlinePointId) throws CustomException {
        if (!productsRepository.existsById(offlinePointProductsDTO.getProductId())) {
            throw new CustomException("Выбранный товар не найден!");
        }
        if (!offlinePointsRepository.existsById(offlinePointId)) {
            throw new CustomException("Указанная оффлайн точка не найдена!");
        }
        Products product = productsRepository.getReferenceById(offlinePointProductsDTO.getProductId());
        OfflinePoints offlinePoint = offlinePointsRepository.getReferenceById(offlinePointId);
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
        if (!offlinePointProductsRepository.existsById(offlinePointProductsDTO.getId())) {
            throw new CustomException("Данный товар отсутствует в списки наличия!");
        }
        if (!productsRepository.existsById(offlinePointProductsDTO.getProductId())) {
            throw new CustomException("Выбранный товар не найден!");
        }
        OfflinePointProducts offlinePointProduct = offlinePointProductsRepository
                .getReferenceById(offlinePointProductsDTO.getId());
        if (!offlinePointsRepository.existsById(offlinePointProduct.getOfflinePoints().getId())) {
            throw new CustomException("Указанная оффлайн точка не найдена!");
        }
        if (offlinePointProductsDTO.getProductId() == offlinePointProduct.getProduct().getId()
                && offlinePointProductsDTO.getQuantity() == offlinePointProduct.getQuantity()) {
            throw new CustomException("Данный товар не нуждается в обновлении!");
        }
        Products product = productsRepository.getReferenceById(offlinePointProductsDTO.getProductId());
        OfflinePointProducts offlinePointProduct1 = new OfflinePointProducts(offlinePointProductsDTO.getQuantity(),
                product, offlinePointProduct.getOfflinePoints());
        offlinePointProduct1.setId(offlinePointProduct.getId());
        offlinePointProductsRepository.save(offlinePointProduct1);
        return offlinePointProductsConvector.convertToDTO(offlinePointProduct);
    }
    public void offlinePointProductsDelete(Long offlinePointProductId) throws CustomException {
        if (!offlinePointProductsRepository.existsById(offlinePointProductId)) {
            throw new CustomException("Данный товар отсутствует в списки наличия!");
        }
        offlinePointProductsRepository.deleteById(offlinePointProductId);
    }
}
