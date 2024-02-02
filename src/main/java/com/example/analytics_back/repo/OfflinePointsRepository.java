package com.example.analytics_back.repo;

import com.example.analytics_back.model.OfflinePoints;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfflinePointsRepository extends JpaRepository<OfflinePoints, Long> {
    boolean existsByName(String name);
}
