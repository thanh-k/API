package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter @Setter @AllArgsConstructor
public class UserDto {
  private Long id; private String name; private String phone; private String email; private String address; private String role; private Instant createdAt; private Instant updatedAt;
}
