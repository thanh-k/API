package com.example.demo.mapper;

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Product;


// ...
public class ProductMapper {

    public static ProductDto toDto(Product p) {
        if (p == null) return null;
        return ProductDto.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .quantity(p.getQuantity())
                .imageUrl(p.getImageUrl())
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null) // Lấy categoryId
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null) // Lấy categoryName
                .build();
    }

    public static Product toEntity(ProductDto d) {
        if (d == null) return null;
        return Product.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .price(d.getPrice())
                .quantity(d.getQuantity())
                .imageUrl(d.getImageUrl())
                // Không set category ở đây, việc này sẽ được Service xử lý
                .build();
    }
}