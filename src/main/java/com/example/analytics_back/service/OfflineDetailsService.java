package com.example.analytics_back.service;

import com.example.analytics_back.DTO.OfflineDetailsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.*;
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
    private final OfflineDetailsRepository offlineDetailsRepository;
    private final OfflinePointProductsRepository offlinePointProductsRepository;
    private final OfflineDetailsDTOConvector offlineDetailsDTOConvector;
    private final OfflineBuysService offlineBuysService;
    private final OfflinePointProductsService offlinePointProductsService;

    public OfflineDetails getOfflineDetail(Long offlineDetailId) {
        return offlineDetailsRepository.findById(offlineDetailId)
                .orElseThrow(() -> new CustomNotFoundException("Невозможно получить информацию " +
                        "о товаре в оффлайн продаже!"));
    }

    public List<OfflineDetailsDTO> getOfflineDetails(Long offlineBuyId) {
        OfflineBuys offlineBuy = offlineBuysService.getOfflineBuy(offlineBuyId);
        List<OfflineDetails> offlineDetails = offlineBuy.getOfflineDetails();
        return offlineDetails.stream()
                .map(offlineDetailsDTOConvector::convertToDTO)
                .toList();
    }

    public OfflineDetailsDTO offlineDetailAdd(Long offlineBuyId, OfflineDetailsDTO offlineDetailsDTO)
            throws CustomException {
        OfflineBuys offlineBuy = offlineBuysService.getOfflineBuy(offlineBuyId);
        OfflinePointProducts offlinePointProduct = offlinePointProductsService
                .getOfflinePointProduct(offlineDetailsDTO.getProductId());
        if (offlinePointProduct.getQuantityReal() < offlineDetailsDTO.getQuantity()) {
            int number = offlineDetailsDTO.getQuantity() - offlinePointProduct.getQuantityReal();
            throw new CustomException("Недостаточно товара \"" + offlinePointProduct.getProduct().getName() + "\". Требуется еще "
                    + number + " шт.");
        }
        offlinePointProduct.setQuantity(offlinePointProduct.getQuantityReal() - offlineDetailsDTO.getQuantity());
        offlinePointProductsRepository.save(offlinePointProduct);

        OfflineDetails offlineDetail = offlineDetailsRepository
                .findByPriceAndOfflineBuyAndOfflinePointProducts(offlineDetailsDTO.getPrice(),
                        offlineBuy, offlinePointProduct);
        if (offlineDetail != null) {
            offlineDetail.setQuantity(offlineDetail.getQuantity() + offlineDetailsDTO.getQuantity());
            offlineDetailsRepository.save(offlineDetail);
            return setOfflineDetailsDTO(offlineDetail, true);
        }
        offlineDetail = new OfflineDetails(offlineBuy,
                offlinePointProduct, offlineDetailsDTO.getQuantity(), offlineDetailsDTO.getPrice());
        offlineDetailsRepository.save(offlineDetail);
        return offlineDetailsDTOConvector.convertToDTO(offlineDetail);
    }

    public void offlineDetailDelete(Long offlineDetailId) {
        OfflineDetails offlineDetail = getOfflineDetail(offlineDetailId);
        OfflinePointProducts offlinePointProducts = offlinePointProductsService
                .getOfflinePointProduct(offlineDetail.getOfflinePointProducts().getId());
        offlinePointProducts.setQuantity(offlinePointProducts.getQuantityReal() + offlineDetail.getQuantity());
        offlinePointProductsRepository.save(offlinePointProducts);
        offlineDetailsRepository.delete(offlineDetail);
    }
    public OfflineDetailsDTO setOfflineDetailsDTO(OfflineDetails offlineDetails, boolean isOldProduct) {
        return new OfflineDetailsDTO(offlineDetails.getId(),
                offlineDetails.getOfflinePointProducts().getProduct().getId(),
                offlineDetails.getQuantity(), offlineDetails.getPrice(), isOldProduct);
    }
}
