package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ApiResponse; // Import lớp mới
import com.example.demo.dto.ProductDto;
import com.example.demo.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    // CREATE
    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody ProductDto dto) {
        service.addProduct(dto); // Thêm sản phẩm
        ApiResponse response = new ApiResponse("Thêm sản phẩm thành công!");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // READ all
    @GetMapping
    public List<ProductDto> getAll() {
        return service.getAllProducts();
    }

    // READ by id
    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable Integer id) {
        return service.getProductById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Integer id, @RequestBody ProductDto dto) {
        service.updateProduct(id, dto); // Cập nhật sản phẩm
        ApiResponse response = new ApiResponse("Cập nhật sản phẩm với ID " + id + " thành công!");
        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id) {
        service.deleteProduct(id); // Xóa sản phẩm
        ApiResponse response = new ApiResponse("Đã xóa thành công sản phẩm có ID " + id);
        return ResponseEntity.ok(response);
    }

    // CÁC PHƯƠNG THỨC TÌM KIẾM GIỮ NGUYÊN
    // SEARCH by name
    @GetMapping("/search")
    public List<ProductDto> searchByName(@RequestParam String keyword) {
        return service.searchByName(keyword);
    }

    // SEARCH by price range
    @GetMapping("/search/price")
    public List<ProductDto> searchByPrice(@RequestParam Double min, @RequestParam Double max) {
        return service.searchByPriceRange(min, max);
    }

    // SEARCH by quantity
    @GetMapping("/search/quantity")
    public List<ProductDto> searchByQuantity(@RequestParam Integer q) {
        return service.searchByQuantityGreaterThan(q);
    }
}