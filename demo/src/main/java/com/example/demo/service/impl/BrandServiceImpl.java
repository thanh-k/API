package com.example.demo.service.impl;

import com.example.demo.dto.BrandDto;
import com.example.demo.entity.Brand;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.BrandMapper;
import com.example.demo.repository.BrandRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    @Override
    public BrandDto createBrand(BrandDto dto) {
        // Create slug nếu chưa có
        if (dto.getSlug() == null || dto.getSlug().isBlank()) {
            dto.setSlug(generateSlug(dto.getName()));
            int i = 1;
            String base = dto.getSlug();
            while (brandRepository.existsBySlug(dto.getSlug())) {
                dto.setSlug(base + "-" + i++);
            }
        } else {
            if (brandRepository.existsBySlug(dto.getSlug())) {
                throw new IllegalArgumentException("Slug đã tồn tại");
            }
        }
        Brand b = BrandMapper.toEntity(dto);
        Brand saved = brandRepository.save(b);
        return BrandMapper.toDto(saved);
    }

    @Override
    public List<BrandDto> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(BrandMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BrandDto getBrandBySlug(String slug) {
        Brand b = brandRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with slug: " + slug));
        return BrandMapper.toDto(b);
    }

    @Override
    public BrandDto updateBrand(Integer id, BrandDto dto) {
        Brand existing = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setLogoUrl(dto.getLogoUrl());
        if (dto.getSlug() != null && !dto.getSlug().isBlank()) {
            existing.setSlug(dto.getSlug());
        } // else giữ nguyên slug
        Brand updated = brandRepository.save(existing);
        return BrandMapper.toDto(updated);
    }

    @Override
public void deleteBrand(Integer id) {

    // kiểm tra brand có tồn tại không
    if (!brandRepository.existsById(id)) {
        throw new ResourceNotFoundException("Brand không tồn tại!");
    }

    // kiểm tra brand có được sử dụng ở product không
    if (productRepository.existsByBrandId(id)) {
        throw new IllegalStateException(
            "Không thể xoá thương hiệu này vì đang có sản phẩm sử dụng!"
        );
    }

    brandRepository.deleteById(id);
}

    // đơn giản tạo slug: chuyển unicode -> ascii, thay khoảng trắng bằng '-',
    // lowercase
    private String generateSlug(String input) {
        if (input == null)
            return null;
        String nowhitespace = input.trim().replaceAll("\\s+", "-").toLowerCase(Locale.ROOT);
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        // loại bỏ dấu
        normalized = normalized.replaceAll("\\p{M}", "");
        normalized = normalized.replaceAll("[^a-z0-9\\-]", "");
        return normalized;
    }

    @Override
    public BrandDto getBrandById(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        return BrandMapper.toDto(brand);
    }

}
