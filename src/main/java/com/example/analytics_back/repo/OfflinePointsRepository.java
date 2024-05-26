package com.example.analytics_back.repo;

import com.example.analytics_back.model.OfflinePoints;
import com.example.analytics_back.model.Regions;
import com.example.analytics_back.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfflinePointsRepository extends JpaRepository<OfflinePoints, Long> {
    boolean existsByName(String name);
    boolean existsByAddressAndRegionAndOwner(String address, Regions regions, Users user);
    OfflinePoints findByAddressAndRegionAndOwner(String address, Regions regions, Users user);
}
