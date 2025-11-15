package com.example.demo.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerDto {
    private Integer id;
    private String name;
    private String imageUrl;
    private String linkUrl;
    private String position;
    private Integer status;
    private MultipartFile file; 
}
