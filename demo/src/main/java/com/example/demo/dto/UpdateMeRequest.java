package com.example.demo.dto;

import lombok.Data;

@Data
public class UpdateMeRequest {
  private String name;
  private String phone;
  private String email;
  private String address;
  private String avatarUrl; // đổi avatar dùng field này
}
