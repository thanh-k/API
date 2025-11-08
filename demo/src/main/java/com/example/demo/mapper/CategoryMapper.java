package com.example.demo.mapper;

import com.example.demo.dto.CategoryDto;
import com.example.demo.entity.Category;

public class CategoryMapper {
    public static CategoryDto toDto(Category c) {
        if (c == null) return null;
        return CategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .imageUrl(c.getImageUrl())
                .build();
    }

    public static Category toEntity(CategoryDto d) {
        if (d == null) return null;
        return Category.builder()
                .id(d.getId())
                .name(d.getName())
                .imageUrl(d.getImageUrl())
                .build();
    }
}