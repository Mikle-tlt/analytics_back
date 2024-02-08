package com.example.analytics_back.repo;
;
import com.example.analytics_back.model.Categories;
import com.example.analytics_back.model.Products;
import com.example.analytics_back.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {
    Products findByNameAndCategory(String name, Categories category);
    Products findByNameAndCategoryAndOwner(String name, Categories category, Users owner);
    boolean existsByNameAndCategoryAndOwner(String name, Categories category, Users owner);
    boolean existsByNameAndOwner(String name, Users owner);

    boolean existsByName(String name);

    Products getReferenceByName(String name);
    List<Products> findAllByOwner(Users userId);
}
