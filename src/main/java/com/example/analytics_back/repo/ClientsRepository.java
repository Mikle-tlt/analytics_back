package com.example.analytics_back.repo;

import com.example.analytics_back.model.Clients;
import com.example.analytics_back.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientsRepository extends JpaRepository<Clients, Long> {
}
