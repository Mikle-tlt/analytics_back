package com.example.analytics_back.service;

import com.example.analytics_back.DTO.DetailsDTO;
import com.example.analytics_back.exception.CustomNotFoundException;
import com.example.analytics_back.model.*;
import com.example.analytics_back.repo.DetailsRepository;
import com.example.analytics_back.service.DTOConvectors.DetailsDTOConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DetailsService {
    private final DetailsRepository detailsRepository;
    private final DetailsDTOConverter detailsDTOConverter;
    private final BuysService buysService;
    private final ProductsService productsService;

    public Details getBuyDetail(Long buyId) {
        return detailsRepository.findById(buyId)
                .orElseThrow(() -> new CustomNotFoundException("Невозможно получить информацию о покупке!"));
    }

    public List<DetailsDTO> getBuyDetails(Long buyId) {
        Buys buy = buysService.getBuy(buyId);
        List<Details> buyDetails = buy.getDetails().stream()
                .sorted(Comparator.comparing(Details::getId))
                .toList();
        return buyDetails.stream()
                .map(detailsDTOConverter::convertToDTO)
                .toList();
    }

    public DetailsDTO buyDetailAdd(Long buyId, DetailsDTO detailsDTO) {
        Buys buy = buysService.getBuy(buyId);
        Products product = productsService.getProduct(detailsDTO.getProductId());
        Details existDetail = detailsRepository.findByProductAndPriceAndBuy(product, detailsDTO.getPrice(), buy);
        if (existDetail != null) {
            Details newDetail = new Details(existDetail.getBuy(), existDetail.getProduct(),
                    existDetail.getQuantity(), existDetail.getPrice());
            newDetail.setQuantity(detailsDTO.getQuantity() + existDetail.getQuantity());
            newDetail.setId(existDetail.getId());
            detailsRepository.save(newDetail);
            return setDetailsDTO(newDetail, true);
        }
        Details details = new Details(buy, product, detailsDTO.getQuantity(), detailsDTO.getPrice());
        detailsRepository.save(details);
        return detailsDTOConverter.convertToDTO(details);
    }

    public DetailsDTO detailEdit(DetailsDTO detailsDTO) {
        Details details = getBuyDetail(detailsDTO.getId());
        Products products = productsService.getProduct(detailsDTO.getProductId());
        Buys buys = buysService.getBuy(details.getBuy().getId());
        Details existDetail = detailsRepository.findByProductAndPriceAndBuy(products, detailsDTO.getPrice(), buys);
        Details updatedDetails = new Details(buys, products, detailsDTO.getQuantity(), detailsDTO.getPrice());
        if (existDetail != null && detailsDTO.getPrice() != details.getPrice()) {
            updatedDetails.setQuantity(detailsDTO.getQuantity() + existDetail.getQuantity());
            updatedDetails.setId(existDetail.getId());
            detailsRepository.save(updatedDetails);
            detailsRepository.deleteById(detailsDTO.getId());
            return setDetailsDTO(updatedDetails, true);
        }
        updatedDetails.setId(detailsDTO.getId());
        detailsRepository.save(updatedDetails);
        return detailsDTOConverter.convertToDTO(details);
    }

    public void detailDelete(Long detailId) {
        Details details = getBuyDetail(detailId);
        detailsRepository.delete(details);
    }

    public DetailsDTO setDetailsDTO(Details details, boolean isOldProduct) {
        return new DetailsDTO(details.getId(), details.getProduct().getId(),
                details.getQuantity(), details.getPrice(), isOldProduct);
    }
}
