package com.example.demo.service.impl;

import com.example.demo.dto.BannerDto;
import com.example.demo.entity.Banner;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.BannerMapper;
import com.example.demo.repository.BannerRepository;
import com.example.demo.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    @Override
    public BannerDto createBanner(BannerDto dto) {

        MultipartFile file = dto.getFile();
        String imageUrl = null;

        try {
            if (file != null && !file.isEmpty()) {

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Lưu file
                Files.copy(file.getInputStream(), uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);

                imageUrl = "/api/files/" + fileName;
            }
        } catch (Exception e) {
            throw new RuntimeException("Upload banner image failed", e);
        }

        Banner b = BannerMapper.toEntity(dto);
        b.setImageUrl(imageUrl);

        Banner saved = bannerRepository.save(b);
        return BannerMapper.toDto(saved);
    }

    @Override
    public BannerDto updateBanner(Integer id, BannerDto dto) {

        Banner b = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found"));

        MultipartFile file = dto.getFile();

        try {
            if (file != null && !file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Files.copy(file.getInputStream(), uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);

                b.setImageUrl("/api/files/" + fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Update banner image failed", e);
        }

        // Update các field khác
        b.setName(dto.getName());
        b.setLinkUrl(dto.getLinkUrl());
        b.setPosition(dto.getPosition());
        b.setStatus(dto.getStatus());

        Banner updated = bannerRepository.save(b);
        return BannerMapper.toDto(updated);
    }

    @Override
    public void deleteBanner(Integer id) {
        if (!bannerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Banner không tồn tại!");
        }
        bannerRepository.deleteById(id);
    }

    @Override
    public BannerDto getBannerById(Integer id) {
        Banner b = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found"));
        return BannerMapper.toDto(b);
    }

    @Override
    public List<BannerDto> getAllBanners(String position) {
        if (position != null && !position.isBlank()) {
            return bannerRepository.findByPosition(position)
                    .stream().map(BannerMapper::toDto).collect(Collectors.toList());
        }
        return bannerRepository.findAll()
                .stream().map(BannerMapper::toDto).collect(Collectors.toList());
    }
}
