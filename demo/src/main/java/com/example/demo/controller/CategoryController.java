package com.example.demo.controller;

import com.example.demo.dto.ApiResponse; // Import lớp mới
import com.example.demo.dto.CategoryDto;
import com.example.demo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // CREATE
    @PostMapping
    public ResponseEntity<ApiResponse> createCategory(@RequestBody CategoryDto categoryDto) {
        categoryService.createCategory(categoryDto);
        ApiResponse response = new ApiResponse("Thêm danh mục thành công!");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // READ all
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // READ by id
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable Integer id, @RequestBody CategoryDto categoryDto) {
        categoryService.updateCategory(id, categoryDto);
        ApiResponse response = new ApiResponse("Cập nhật danh mục với ID " + id + " thành công!");
        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        ApiResponse response = new ApiResponse("Đã xóa thành công danh mục có ID " + id);
        return ResponseEntity.ok(response);
    }
}