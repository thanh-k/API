package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ProductDto;
import com.example.demo.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    // ============ READ (ai cũng xem) ============
    @GetMapping
    public List<ProductDto> getAll() {
        return service.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable Integer id) {
        return service.getProductById(id);
    }

    @GetMapping("/search")
    public List<ProductDto> searchByName(@RequestParam String keyword) {
        return service.searchByName(keyword);
    }

    @GetMapping("/search/price")
    public List<ProductDto> searchByPrice(@RequestParam Double min, @RequestParam Double max) {
        return service.searchByPriceRange(min, max);
    }

    @GetMapping("/search/quantity")
    public List<ProductDto> searchByQuantity(@RequestParam Integer q) {
        return service.searchByQuantityGreaterThan(q);
    }

    // ============ WRITE (ADMIN) ============
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody ProductDto dto) {
        service.addProduct(dto);
        return new ResponseEntity<>(new ApiResponse("Thêm sản phẩm thành công!"), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Integer id, @RequestBody ProductDto dto) {
        service.updateProduct(id, dto);
        return ResponseEntity.ok(new ApiResponse("Cập nhật sản phẩm với ID " + id + " thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id) {
        service.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse("Đã xóa thành công sản phẩm có ID " + id));
    }
}
