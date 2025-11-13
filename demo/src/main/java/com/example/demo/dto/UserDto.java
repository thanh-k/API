package com.example.demo.dto;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String role;       // chuỗi "ADMIN"/"STAFF"/"USER"
    private String avatarUrl;
    private Instant createdAt;
    private Instant updatedAt;
}
