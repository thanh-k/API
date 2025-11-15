package com.example.demo.controller;

import com.example.demo.dto.BannerDto;
import com.example.demo.service.BannerService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banners")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    public List<BannerDto> getAll(@RequestParam(required = false) String position) {
        return bannerService.getAllBanners(position);
    }

    @GetMapping("/{id}")
    public BannerDto getById(@PathVariable Integer id) {
        return bannerService.getBannerById(id);
    }

    // ====== CREATE (MULTIPART) ======
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBanner(@ModelAttribute BannerDto dto) {
        return ResponseEntity.ok(bannerService.createBanner(dto));
    }

    // ====== UPDATE (MULTIPART) ======
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateBanner(
            @PathVariable Integer id,
            @ModelAttribute BannerDto dto
    ) {
        return ResponseEntity.ok(bannerService.updateBanner(id, dto));
    }

    // ====== DELETE ======
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        bannerService.deleteBanner(id);
        return "Banner đã được xoá!";
    }
}
