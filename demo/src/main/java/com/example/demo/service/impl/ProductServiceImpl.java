package com.example.demo.service.impl;

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Product;
import com.example.demo.entity.Category;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;
    private final CategoryRepository categoryRepo; // nếu bạn không dùng category, có thể xoá field này

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
        e = repo.save(e);
        return toDto(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return repo.findAll().stream().map(this::toDto).toList();
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
        return repo.findByNameContainingIgnoreCase(keyword).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> searchByPriceRange(Double min, Double max) {
        return repo.findByPriceBetween(min, max).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> searchByQuantityGreaterThan(Integer q) {
        return repo.findByQuantityGreaterThan(q).stream().map(this::toDto).toList();
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
}
