package com.example.demo.controller;

import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.RoleUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {
  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public ResponseEntity<?> get(@AuthenticationPrincipal CustomUserDetails principal){
    var u = principal.getDomainUser();
    var full = Map.of("totalProducts", 1200, "totalOrders", 3500, "revenue", 999999.0, "profit", 123456.0);
    if (!RoleUtils.isAdmin(u)) return ResponseEntity.ok(Map.of("totalProducts", full.get("totalProducts"), "totalOrders", full.get("totalOrders")));
    return ResponseEntity.ok(full);
  }
}
