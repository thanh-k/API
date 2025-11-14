package com.example.demo.repository;

import com.example.demo.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand,Integer> {
    Optional<Brand> findBySlug(String slug);
    boolean existsBySlug(String slug);
    boolean existsByName(String name);
}
