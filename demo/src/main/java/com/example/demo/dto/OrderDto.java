package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long id;
    private Long userId;

    private String name;
    private String phone;
    private String email;
    private String address;

    private LocalDateTime createdAt;
    private Double totalAmount;

    private List<Item> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private Long id;              // id của OrderDetail
        private Integer productId;    // ⬅ QUAN TRỌNG: Integer để khớp Product.id
        private String productName;
        private Integer quantity;
        private Double price;         // line total
    }
}
