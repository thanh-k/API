package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateUserRequest {
  @NotBlank private String name;
  private String phone;
  @Email @NotBlank private String email;
  private String address;
  private String password; // optional
  @NotBlank private String role; // ADMIN | STAFF | USER
}
