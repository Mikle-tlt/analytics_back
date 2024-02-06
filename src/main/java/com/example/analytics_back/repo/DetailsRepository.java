package com.example.analytics_back.repo;

import com.example.analytics_back.model.Buys;
import com.example.analytics_back.model.Details;
import com.example.analytics_back.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetailsRepository extends JpaRepository<Details, Long> {
    boolean existsByProductAndBuy(Products product, Buys buy);
    Details findByProductAndBuy(Products product, Buys buy);
    boolean existsByProductAndPrice(Products product, Double price);
    Details findByProductAndPriceAndBuy(Products product, Double price, Buys buys);
}
