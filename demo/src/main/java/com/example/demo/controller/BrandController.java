package com.example.demo.controller;

import com.example.demo.dto.BrandDto;
import com.example.demo.dto.ApiResponse;
import com.example.demo.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor

public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<List<BrandDto>> getAll() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    // Lấy theo ID (cho Admin Edit Page)
    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    // Lấy theo slug (cho trang User)
    @GetMapping("/slug/{slug}")
    public ResponseEntity<BrandDto> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(brandService.getBrandBySlug(slug));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BrandDto> create(@RequestBody BrandDto dto) {
        BrandDto created = brandService.createBrand(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Integer id, @RequestBody BrandDto dto) {
        brandService.updateBrand(id, dto);
        return ResponseEntity.ok(new ApiResponse("Cập nhật thương hiệu thành công!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(new ApiResponse("Đã xóa thương hiệu"));
    }
}
