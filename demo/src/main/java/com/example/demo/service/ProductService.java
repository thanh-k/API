package com.example.demo.service;

import com.example.demo.dto.ProductDto;
import java.util.List;

public interface ProductService {
    ProductDto addProduct(ProductDto productDto);
    List<ProductDto> getAllProducts();
    ProductDto getProductById(Integer id);           // chỉ khai báo
    ProductDto updateProduct(Integer id, ProductDto productDto);
    void deleteProduct(Integer id);

    // 3 chức năng query
    List<ProductDto> searchByName(String keyword);
    List<ProductDto> searchByPriceRange(Double min, Double max);
    List<ProductDto> searchByQuantityGreaterThan(Integer q);
}
