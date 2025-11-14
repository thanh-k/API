package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDto {

    private String name;
    private String phone;
    private String email;
    private String address;

    private List<Item> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        // ⬅ cũng dùng Integer cho khớp ProductRepository<Product, Integer>
        private Integer productId;
        private Integer quantity;
    }
}
