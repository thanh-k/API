package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.JwtCookieUtil;
import com.example.demo.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final UserRepository userRepo;
  private final PasswordEncoder enc;
  private final AuthenticationManager authManager;
  private final JwtService jwt;

  public AuthController(UserRepository r, PasswordEncoder e, AuthenticationManager am, JwtService j) {
    this.userRepo = r;
    this.enc = e;
    this.authManager = am;
    this.jwt = j;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
    if (userRepo.existsByEmail(req.getEmail())) {
      return ResponseEntity.badRequest().body(Map.of("error", "Email already in use"));
    }
    if (req.getPhone() != null && !req.getPhone().isBlank() && userRepo.existsByPhone(req.getPhone())) {
      return ResponseEntity.badRequest().body(Map.of("error", "Phone already in use"));
    }

    var u = User.builder()
        .name(req.getName())
        .email(req.getEmail())
        .phone(req.getPhone())
        .address(req.getAddress())
        .password(enc.encode(req.getPassword()))
        .role(User.Role.USER)            // mặc định USER
        .avatarUrl(req.getAvatarUrl())
        .build();

    userRepo.save(u);
    return ResponseEntity.status(201).body(Map.of("message", "Registered successfully"));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req,
                                 HttpServletResponse res,
                                 @Value("${app.jwt.expiration-ms}") long expMs) {
    try {
      authManager.authenticate(
          new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
    } catch (BadCredentialsException ex) {
      return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    var u = userRepo.findByEmail(req.getEmail()).orElse(null);
    if (u == null) {
      return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    String token = jwt.generate(
        u.getEmail(),
        Map.of("uid", u.getId(), "role", u.getRole().name())
    );

    // Ghi JWT vào cookie HttpOnly (tuỳ bạn bật/tắt)
    JwtCookieUtil.writeJwt(res, token, false, (int) (expMs / 1000));

    String next = (u.getRole() == User.Role.ADMIN || u.getRole() == User.Role.STAFF) ? "/admin" : "/";

    // ✅ Trả kèm vai trò để FE điều hướng/ẩn nút ngay lập tức
    return ResponseEntity.ok(new AuthResponse(token, next, u.getRole().name()));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletResponse res) {
    JwtCookieUtil.clear(res, false);
    return ResponseEntity.ok(Map.of("message", "Logged out"));
  }

  @GetMapping("/me")
  public ResponseEntity<?> me(@AuthenticationPrincipal CustomUserDetails principal) {
    if (principal == null) {
      return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
    }
    var u = principal.getDomainUser();
    var body = new java.util.HashMap<String, Object>();
    body.put("id", u.getId());
    body.put("name", u.getName());
    body.put("email", u.getEmail());
    body.put("role", u.getRole());           // vẫn trả role để FE cross-check
    body.put("avatarUrl", u.getAvatarUrl());
    return ResponseEntity.ok(body);
  }
  @PutMapping("/profile")
public ResponseEntity<?> updateProfile(@AuthenticationPrincipal CustomUserDetails principal,
                                       @RequestBody Map<String, Object> body) {
  if (principal == null) return ResponseEntity.status(401).body(Map.of("error","Unauthorized"));
  var u = principal.getDomainUser();
  u.setName((String) body.getOrDefault("name", u.getName()));
  u.setPhone((String) body.getOrDefault("phone", u.getPhone()));
  u.setAddress((String) body.getOrDefault("address", u.getAddress()));
  var av = (String) body.get("avatarUrl");
  if (av != null) u.setAvatarUrl(av);
  userRepo.save(u);
  return ResponseEntity.ok(Map.of("message","Profile updated"));
}

// POST /auth/change-password
@PostMapping("/change-password")
public ResponseEntity<?> changePassword(@AuthenticationPrincipal CustomUserDetails principal,
                                        @RequestBody Map<String,String> req) {
  if (principal == null) return ResponseEntity.status(401).body(Map.of("error","Unauthorized"));
  var u = principal.getDomainUser();
  String oldP = req.get("oldPassword");
  String newP = req.get("newPassword");
  if (!enc.matches(oldP, u.getPassword()))
    return ResponseEntity.status(400).body(Map.of("error","Mật khẩu hiện tại không đúng"));
  u.setPassword(enc.encode(newP));
  userRepo.save(u);
  return ResponseEntity.ok(Map.of("message","Password changed"));
}
}
