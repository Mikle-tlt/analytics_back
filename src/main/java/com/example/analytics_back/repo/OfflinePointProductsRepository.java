package com.example.analytics_back.repo;

import com.example.analytics_back.model.OfflinePointProducts;
import com.example.analytics_back.model.OfflinePoints;
import com.example.analytics_back.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfflinePointProductsRepository extends JpaRepository<OfflinePointProducts, Long> {
    boolean existsByProductAndOfflinePoints(Products product, OfflinePoints offlinePoint);
    OfflinePointProducts findByOfflinePointsAndProductId(OfflinePoints offlinePoint, Long productId);
    boolean existsByOfflinePointsAndProductId(OfflinePoints offlinePoint, Long productId);
}
