package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    // 3 phương thức truy vấn theo yêu cầu
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findByPriceBetween(Double min, Double max);
    List<Product> findByQuantityGreaterThan(Integer q);
}
