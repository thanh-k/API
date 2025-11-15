package com.example.demo.service;

import com.example.demo.dto.BannerDto;

import java.util.List;

public interface BannerService {
    BannerDto createBanner(BannerDto dto);
    BannerDto updateBanner(Integer id, BannerDto dto);
    void deleteBanner(Integer id);
    BannerDto getBannerById(Integer id);
    List<BannerDto> getAllBanners(String position);
}
