package com.example.analytics_back.repo;

import com.example.analytics_back.model.Regions;
import com.example.analytics_back.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionsRepository extends JpaRepository<Regions, Long> {
    Regions findRegionsByNameAndOwner(String name, Users user);
    boolean existsByNameAndOwner(String name, Users user);
}
