package com.example.analytics_back.repo;

import com.example.analytics_back.model.Points;
import com.example.analytics_back.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointsRepository extends JpaRepository<Points, Long> {
    boolean existsByAddressAndOwner(String address, Users user);
    Points findByAddressAndOwner(String address, Users user);
}
