package com.example.analytics_back.repo;

import com.example.analytics_back.model.Buys;
import com.example.analytics_back.model.Clients;
import com.example.analytics_back.model.Points;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface BuysRepository extends JpaRepository<Buys, Long> {
    boolean existsByDateAndPointsAndClient(Date date, Points points, Clients clients);
    Buys findByDateAndPointsAndClient(Date date, Points points, Clients clients);
    List<Buys> findAllByDateAndClient(Date date, Clients clients);
}
