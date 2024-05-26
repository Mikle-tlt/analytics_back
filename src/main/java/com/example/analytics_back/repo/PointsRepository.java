package com.example.analytics_back.repo;

import com.example.analytics_back.model.Points;
import com.example.analytics_back.model.Regions;
import com.example.analytics_back.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointsRepository extends JpaRepository<Points, Long> {
    boolean existsByAddressAndRegionAndOwner(String address, Regions regions, Users user);
    Points findPointsByAddressAndRegionAndOwner(String address, Regions regions, Users user);
}
