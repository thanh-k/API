package com.example.demo.service.impl;

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.entity.Brand;
import com.example.demo.entity.Category;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.BrandRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    // CHỈ KHAI BÁO 1 ProductRepository (mình gọi là repo)
    private final ProductRepository repo;
    private final CategoryRepository categoryRepo; // bạn đang dùng tên này trong code
    private final BrandRepository brandRepository;

    /* ========== CRUD ========== */

    @Override
    public ProductDto addProduct(ProductDto dto) {
        Product e = toEntity(dto);
        // gán category nếu có
        if (dto.getCategoryId() != null) {
            Category c = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found"));
            e.setCategory(c);
        }

        // gán brand nếu có
        if (dto.getBrandId() != null) {
            Brand b = brandRepository.findById(dto.getBrandId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brand not found"));
            e.setBrand(b);
        }

        e = repo.save(e);
        return toDto(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(Integer id) {
        var p = repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return toDto(p);
    }

    @Override
    public ProductDto updateProduct(Integer id, ProductDto dto) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        // cập nhật field
        p.setName(dto.getName());
        p.setPrice(dto.getPrice());
        p.setQuantity(dto.getQuantity());
        p.setDescription(dto.getDescription());
        p.setImageUrl(dto.getImageUrl());

        if (dto.getCategoryId() != null) {
            Category c = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found"));
            p.setCategory(c);
        } else {
            p.setCategory(null);
        }

        if (dto.getBrandId() != null) {
            Brand b = brandRepository.findById(dto.getBrandId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brand not found"));
            p.setBrand(b);
        } else {
            p.setBrand(null);
        }

        p = repo.save(p);
        return toDto(p);
    }

    @Override
    public void deleteProduct(Integer id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        repo.deleteById(id);
    }

    /* ========== SEARCH ========== */

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> searchByName(String keyword) {
        return repo.findByNameContainingIgnoreCase(keyword).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> searchByPriceRange(Double min, Double max) {
        return repo.findByPriceBetween(min, max).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> searchByQuantityGreaterThan(Integer q) {
        return repo.findByQuantityGreaterThan(q).stream().map(this::toDto).collect(Collectors.toList());
    }

    /* ========== MAPPER ========== */

    private ProductDto toDto(Product e) {
        ProductDto dto = new ProductDto();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setPrice(e.getPrice());
        dto.setQuantity(e.getQuantity());
        dto.setDescription(e.getDescription());
        dto.setImageUrl(e.getImageUrl());

        if (e.getCategory() != null) {
            dto.setCategoryId(e.getCategory().getId());
            dto.setCategoryName(e.getCategory().getName()); // nếu ProductDto có field này
        }

        if (e.getBrand() != null) {
            dto.setBrandId(e.getBrand().getId());
            dto.setBrandName(e.getBrand().getName());
            dto.setBrandSlug(e.getBrand().getSlug());
        }

        return dto;
    }

    private Product toEntity(ProductDto dto) {
        Product e = new Product();
        e.setId(dto.getId());
        e.setName(dto.getName());
        e.setPrice(dto.getPrice());
        e.setQuantity(dto.getQuantity());
        e.setDescription(dto.getDescription());
        e.setImageUrl(dto.getImageUrl());
        return e;
    }

    // Lọc theo brand slug
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByBrandSlug(String brandSlug) {
        Brand brand = brandRepository.findBySlug(brandSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found: " + brandSlug));

        return repo.findByBrand(brand)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}
