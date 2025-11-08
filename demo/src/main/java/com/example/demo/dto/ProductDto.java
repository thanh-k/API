package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private String imageUrl;
    private Integer categoryId; // Thêm trường này để nhận id của category khi tạo/cập nhật product
    private String categoryName; // Thêm trường này để hiển thị tên category

}
