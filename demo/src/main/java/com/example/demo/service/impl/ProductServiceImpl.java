package com.example.demo.service.impl;

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;
    private final CategoryRepository categoryRepo;

    public ProductServiceImpl(ProductRepository repo, CategoryRepository categoryRepo) {
        this.repo = repo;
        this.categoryRepo = categoryRepo;
    }

    @Override
    public ProductDto addProduct(ProductDto dto) {
        Product product = ProductMapper.toEntity(dto);

        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        product.setCategory(category);
        
        Product saved = repo.save(product);
        return ProductMapper.toDto(saved);
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return repo.findAll().stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductById(Integer id) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductMapper.toDto(p);
    }

    @Override
    public ProductDto updateProduct(Integer id, ProductDto dto) {
        Product existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Cập nhật các trường thông tin
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setQuantity(dto.getQuantity());
        existing.setImageUrl(dto.getImageUrl());
        
        // Kiểm tra và cập nhật category nếu có thay đổi (chỉ cần làm một lần)
        if (dto.getCategoryId() != null && !dto.getCategoryId().equals(existing.getCategory().getId())) {
            Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
            existing.setCategory(category);
        }
        
        Product updated = repo.save(existing);
        return ProductMapper.toDto(updated);
    }

    @Override
    public void deleteProduct(Integer id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    public List<ProductDto> searchByName(String keyword) {
        return repo.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> searchByPriceRange(Double min, Double max) {
        return repo.findByPriceBetween(min, max)
                .stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> searchByQuantityGreaterThan(Integer q) {
        return repo.findByQuantityGreaterThan(q)
                .stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }
}