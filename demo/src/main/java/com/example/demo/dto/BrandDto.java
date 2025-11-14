package com.example.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandDto {
    private Integer id;
    private String name;
    private String slug;
    private String logoUrl;
    private String description;
}
