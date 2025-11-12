package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateUserRequest {
  @NotBlank private String name;
  private String phone;
  @Email @NotBlank private String email;
  private String address;
  @NotBlank @Size(min=6) private String password;
  @NotBlank private String role; // ADMIN | STAFF | USER
}
