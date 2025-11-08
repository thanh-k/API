package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.ProductDto;

public interface ProductService {
    ProductDto addProduct(ProductDto productDto);
    List<ProductDto> getAllProducts();
    ProductDto getProductById(Integer id);
    ProductDto updateProduct(Integer id, ProductDto productDto);
    void deleteProduct(Integer id);

    // 3 chức năng query
    List<ProductDto> searchByName(String keyword);
    List<ProductDto> searchByPriceRange(Double min, Double max);
    List<ProductDto> searchByQuantityGreaterThan(Integer q);
}
