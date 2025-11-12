package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {
  @Bean
  CommandLineRunner initUsers(UserRepository userRepo, PasswordEncoder enc) {
    return args -> {
      userRepo.findByEmail("admin@example.com").orElseGet(() ->
        userRepo.save(User.builder()
            .name("Admin")
            .email("admin@example.com")
            .phone("0909")
            .address("HN")
            .password(enc.encode("admin123"))
            .role(User.Role.ADMIN)
            .build())
      );

      userRepo.findByEmail("staff@example.com").orElseGet(() ->
        userRepo.save(User.builder()
            .name("Staff")
            .email("staff@example.com")
            .phone("0910")
            .address("HCM")
            .password(enc.encode("staff123"))
            .role(User.Role.STAFF)
            .build())
      );
    };
  }
}
