package com.example.analytics_back.service;

import com.example.analytics_back.DTO.OfflineDetailsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.OfflineBuys;
import com.example.analytics_back.model.OfflineDetails;
import com.example.analytics_back.model.OfflinePointProducts;
import com.example.analytics_back.repo.OfflineBuysRepository;
import com.example.analytics_back.repo.OfflineDetailsRepository;
import com.example.analytics_back.repo.OfflinePointProductsRepository;
import com.example.analytics_back.service.DTOConvectors.OfflineDetailsDTOConvector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OfflineDetailsService {

    private final OfflineBuysRepository offlineBuysRepository;
    private final OfflineDetailsRepository offlineDetailsRepository;
    private final OfflinePointProductsRepository offlinePointProductsRepository;
    private final OfflineDetailsDTOConvector offlineDetailsDTOConvector;

    public List<OfflineDetails> offlineDetails(Long offlineBuyId) throws CustomException {
        if (!offlineBuysRepository.existsById(offlineBuyId)) {
            throw new CustomException("Указанная продажа не найдена!");
        }
        OfflineBuys offlineBuy = offlineBuysRepository.findById(offlineBuyId).orElseThrow();
        return offlineBuy.getOfflineDetails();
    }

    public OfflineDetailsDTO offlineDetailAdd(Long offlineBuyId, OfflineDetailsDTO offlineDetailsDTO) throws CustomException {
        if (!offlineBuysRepository.existsById(offlineBuyId)) {
            throw new CustomException("Указанная продажа не найдена!");
        }
        if (!offlinePointProductsRepository.existsById(offlineDetailsDTO.getProductId())) {
            throw new CustomException("Указанная товар не найден!");
        }
        OfflineBuys offlineBuy = offlineBuysRepository.getReferenceById(offlineBuyId);
        OfflinePointProducts offlinePointProducts = offlinePointProductsRepository
                .getReferenceById(offlineDetailsDTO.getProductId());
        if (offlinePointProducts.getQuantityReal() < offlineDetailsDTO.getQuantity()) {
            throw new CustomException("Недостаточно товаров!");
        }
        offlinePointProducts.setQuantity(offlinePointProducts.getQuantityReal() - offlineDetailsDTO.getQuantity());
        offlinePointProductsRepository.save(offlinePointProducts);

        OfflineDetails offlineDetails = new OfflineDetails(offlineBuy,
                offlinePointProducts, offlineDetailsDTO.getQuantity(), offlineDetailsDTO.getPrice());

        offlineDetailsRepository.save(offlineDetails);
        return offlineDetailsDTOConvector.convertToDTO(offlineDetails);
    }

    public void offlineDetailsDelete(Long offlineDetailId) throws CustomException {
        if (!offlineDetailsRepository.existsById(offlineDetailId)) {
            throw new CustomException("Товар не найден!");
        }
        OfflineDetails offlineDetails = offlineDetailsRepository.getReferenceById(offlineDetailId);
        OfflinePointProducts offlinePointProducts = offlinePointProductsRepository
                .getReferenceById(offlineDetails.getOfflinePointProducts().getId());
        offlinePointProducts.setQuantity(offlinePointProducts.getQuantityReal() + offlineDetails.getQuantity());
        offlinePointProductsRepository.save(offlinePointProducts);
        offlineDetailsRepository.deleteById(offlineDetailId);
    }
}
