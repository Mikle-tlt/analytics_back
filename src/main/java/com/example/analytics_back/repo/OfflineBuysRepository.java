package com.example.analytics_back.repo;

import com.example.analytics_back.model.OfflineBuys;
import com.example.analytics_back.model.OfflinePoints;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface OfflineBuysRepository extends JpaRepository<OfflineBuys, Long> {
    boolean existsByDateAndOfflinePoints(Date date, OfflinePoints offlinePoint);
    OfflineBuys findByDateAndOfflinePoints(Date date, OfflinePoints offlinePoint);
}
