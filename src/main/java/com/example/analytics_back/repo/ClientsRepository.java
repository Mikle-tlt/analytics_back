package com.example.analytics_back.repo;

import com.example.analytics_back.model.Clients;
import com.example.analytics_back.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientsRepository extends JpaRepository<Clients, Long> {
    boolean existsByNameAndContactAndOwner(String name, String contact, Users user);
    Clients findByNameAndContactAndOwner(String name, String contact, Users user);
}
