package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

  @NotBlank(message = "Vui lòng nhập họ tên.")
  private String name;

  @Email(message = "Email không hợp lệ.")
  @NotBlank(message = "Vui lòng nhập email.")
  private String email;

  @NotBlank(message = "Vui lòng nhập số điện thoại.")
  @Pattern(regexp = "^\\d{9,11}$", message = "Số điện thoại phải là 9–11 chữ số.")
  private String phone;

  @NotBlank(message = "Vui lòng nhập địa chỉ.")
  private String address;

  @NotBlank(message = "Vui lòng nhập mật khẩu.")
  @Size(min = 6, message = "Mật khẩu tối thiểu 6 ký tự.")
  private String password;

  private String avatarUrl;
}
