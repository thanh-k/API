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

    // CHỈ KHAI BÁO 1 biến service và dùng nó ở mọi chỗ
    private final ProductService productService;

    // ============ READ (ai cũng xem) ============
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAll(
            @RequestParam(required = false) String brand) {
        if (brand != null && !brand.isBlank()) {
            return ResponseEntity.ok(productService.getProductsByBrandSlug(brand));
        }
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }

    @GetMapping("/search")
    public List<ProductDto> searchByName(@RequestParam String keyword) {
        return productService.searchByName(keyword);
    }

    @GetMapping("/search/price")
    public List<ProductDto> searchByPrice(@RequestParam Double min, @RequestParam Double max) {
        return productService.searchByPriceRange(min, max);
    }

    @GetMapping("/search/quantity")
    public List<ProductDto> searchByQuantity(@RequestParam Integer q) {
        return productService.searchByQuantityGreaterThan(q);
    }

    // ============ WRITE (ADMIN) ============
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestBody ProductDto dto) {
        productService.addProduct(dto);
        return new ResponseEntity<>(new ApiResponse("Thêm sản phẩm thành công!"), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Integer id, @RequestBody ProductDto dto) {
        productService.updateProduct(id, dto);
        return ResponseEntity.ok(new ApiResponse("Cập nhật sản phẩm với ID " + id + " thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse("Đã xóa thành công sản phẩm có ID " + id));
    }
}
