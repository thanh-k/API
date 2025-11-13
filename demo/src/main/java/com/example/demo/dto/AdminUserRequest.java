package com.example.demo.dto;

import com.example.demo.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserRequest {

    private String name;
    private String email;
    private String phone;
    private String address;
    private String password;   // khi update có thể để trống => không đổi
    private User.Role role;    // ADMIN / STAFF / USER
    private String avatarUrl;
}
