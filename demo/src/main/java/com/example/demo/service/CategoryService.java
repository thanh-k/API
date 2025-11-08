package com.example.demo.service;

import com.example.demo.dto.CategoryDto;
import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);
    CategoryDto getCategoryById(Integer id);
    List<CategoryDto> getAllCategories();
    CategoryDto updateCategory(Integer id, CategoryDto categoryDto);
    void deleteCategory(Integer id);
}