package com.example.demo.service;

import com.example.demo.dto.BrandDto;
import java.util.List;

public interface BrandService {
    BrandDto createBrand(BrandDto dto);
    List<BrandDto> getAllBrands();
    BrandDto getBrandBySlug(String slug);
    BrandDto updateBrand(Integer id, BrandDto dto);
    void deleteBrand(Integer id);
    BrandDto getBrandById(Integer id);
    

}
