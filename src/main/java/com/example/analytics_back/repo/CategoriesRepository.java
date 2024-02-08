package com.example.analytics_back.repo;

import com.example.analytics_back.model.Categories;
import com.example.analytics_back.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {
    boolean existsByNameAndOwner(String name, Users users);
    Categories findByNameAndOwner(String name, Users users);
}
