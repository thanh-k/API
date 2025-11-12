package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
  private String token; // JWT
  private String next;  // gợi ý điều hướng FE: "/admin" hoặc "/"
  private String role;  // ADMIN | STAFF | USER
}
