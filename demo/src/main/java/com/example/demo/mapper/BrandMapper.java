package com.example.demo.mapper;

import com.example.demo.dto.BrandDto;
import com.example.demo.entity.Brand;

public class BrandMapper {
    public static BrandDto toDto(Brand b){
        if (b==null) return null;
        return BrandDto.builder()
                .id(b.getId())
                .name(b.getName())
                .slug(b.getSlug())
                .logoUrl(b.getLogoUrl())
                .description(b.getDescription())
                .build();
    }

    public static Brand toEntity(BrandDto d){
        if (d==null) return null;
        return Brand.builder()
                .id(d.getId())
                .name(d.getName())
                .slug(d.getSlug())
                .logoUrl(d.getLogoUrl())
                .description(d.getDescription())
                .build();
    }
}
