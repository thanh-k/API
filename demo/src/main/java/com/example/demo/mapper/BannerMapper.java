package com.example.demo.mapper;

import com.example.demo.dto.BannerDto;
import com.example.demo.entity.Banner;

public class BannerMapper {

    public static BannerDto toDto(Banner b) {
        if (b == null) return null;
        return BannerDto.builder()
                .id(b.getId())
                .name(b.getName())
                .imageUrl(b.getImageUrl())
                .linkUrl(b.getLinkUrl())
                .position(b.getPosition())
                .status(b.getStatus())
                .build();
    }

    public static Banner toEntity(BannerDto d) {
        if (d == null) return null;
        return Banner.builder()
                .id(d.getId())
                .name(d.getName())
                .imageUrl(d.getImageUrl())
                .linkUrl(d.getLinkUrl())
                .position(d.getPosition())
                .status(d.getStatus())
                .build();
    }
}
