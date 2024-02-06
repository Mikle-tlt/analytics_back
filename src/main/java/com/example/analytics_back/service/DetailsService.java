package com.example.analytics_back.service;

import com.example.analytics_back.DTO.DetailsDTO;
import com.example.analytics_back.DTO.OfflineDetailsDTO;
import com.example.analytics_back.exception.CustomException;
import com.example.analytics_back.model.*;
import com.example.analytics_back.repo.BuysRepository;
import com.example.analytics_back.repo.DetailsRepository;
import com.example.analytics_back.repo.ProductsRepository;
import com.example.analytics_back.service.DTOConvectors.DetailsDTOConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DetailsService {

    private final BuysRepository buysRepository;
    private final ProductsRepository productsRepository;
    private final DetailsRepository detailsRepository;
    private final DetailsDTOConverter detailsDTOConverter;

    public List<Details> details(Long buyId) throws CustomException {
        if (!buysRepository.existsById(buyId)) {
            throw new CustomException("Указанная покупка не найдена!");
        }
        Buys buys = buysRepository.findById(buyId).orElseThrow();
        return buys.getDetails().stream()
                .sorted(Comparator.comparing(Details::getId))
                .collect(Collectors.toList());
    }

    public DetailsDTO detailAdd(Long buyId, DetailsDTO detailsDTO) throws CustomException {
        if (!buysRepository.existsById(buyId)) {
            throw new CustomException("Указанная покупка не найдена!");
        }
        if (!productsRepository.existsById(detailsDTO.getProductId())) {
            throw new CustomException("Указанный товар не найден!");
        }
        Products products = productsRepository.getReferenceById(detailsDTO.getProductId());
        Buys buys = buysRepository.getReferenceById(buyId);
        Details existDetail = detailsRepository.findByProductAndPriceAndBuy(products, detailsDTO.getPrice(), buys);
        if (existDetail != null) {
            Details newDetail = new Details(existDetail.getBuy(), existDetail.getProduct(),
                    existDetail.getQuantity(), existDetail.getPrice());
            newDetail.setQuantity(detailsDTO.getQuantity() + existDetail.getQuantity());
            newDetail.setId(existDetail.getId());
            detailsRepository.save(newDetail);
            return setDetailsDTO(newDetail, true);
        }
        Details details = new Details(buys, products, detailsDTO.getQuantity(), detailsDTO.getPrice());
        detailsRepository.save(details);
        return detailsDTOConverter.convertToDTO(details);
    }

    public DetailsDTO detailEdit(DetailsDTO detailsDTO) throws CustomException {
        if (!detailsRepository.existsById(detailsDTO.getId())) {
            throw new CustomException("Детальная информация о покупке не найдена!");
        }
        if (!productsRepository.existsById(detailsDTO.getProductId())) {
            throw new CustomException("Указанный товар не найден!");
        }
        Details details = detailsRepository.getReferenceById(detailsDTO.getId());
        Products products = productsRepository.getReferenceById(detailsDTO.getProductId());
        Buys buys = buysRepository.getReferenceById(details.getBuy().getId());

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

    public void detailDelete(Long detailId) throws CustomException {
        if (!detailsRepository.existsById(detailId)) {
            throw new CustomException("Детальная информация о покупке не найдена!");
        }
        detailsRepository.deleteById(detailId);
    }

    public DetailsDTO setDetailsDTO(Details details, boolean isOldProduct) {
        return new DetailsDTO(details.getId(), details.getProduct().getId(),
                details.getQuantity(), details.getPrice(), isOldProduct);
    }
}
