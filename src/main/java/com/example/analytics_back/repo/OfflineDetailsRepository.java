package com.example.analytics_back.repo;

import com.example.analytics_back.model.OfflineBuys;
import com.example.analytics_back.model.OfflineDetails;
import com.example.analytics_back.model.OfflinePointProducts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfflineDetailsRepository extends JpaRepository<OfflineDetails, Long> {
    OfflineDetails findByPriceAndOfflineBuyAndOfflinePointProducts(Double price, OfflineBuys offlineBuy,
                                                                   OfflinePointProducts offlinePointProduct);
}
