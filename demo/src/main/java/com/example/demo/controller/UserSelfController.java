package com.example.demo.controller;

import com.example.demo.dto.UpdateMeRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/users") // => context-path /api sẽ tự prepend thành /api/users
@RequiredArgsConstructor
public class UserSelfController {

  private final UserRepository userRepo;

  @PutMapping("/me")
  public ResponseEntity<?> updateMe(@AuthenticationPrincipal CustomUserDetails principal,
                                    @RequestBody UpdateMeRequest req) {
    if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");

    Long myId = principal.getDomainUser().getId();
    User u = userRepo.findById(myId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    // kiểm tra email trùng (nếu có đổi email)
    if (req.getEmail() != null && !req.getEmail().equalsIgnoreCase(u.getEmail())
        && userRepo.existsByEmail(req.getEmail())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
    }

    // cập nhật có điều kiện
    if (req.getName() != null)     u.setName(req.getName());
    if (req.getPhone() != null)    u.setPhone(req.getPhone());
    if (req.getEmail() != null)    u.setEmail(req.getEmail());
    if (req.getAddress() != null)  u.setAddress(req.getAddress());
    if (req.getAvatarUrl() != null)u.setAvatarUrl(req.getAvatarUrl());

    userRepo.save(u);

    // trả về gọn để FE cập nhật lại state
    return ResponseEntity.ok(Map.of(
        "message", "Updated",
        "user", Map.of(
            "id", u.getId(),
            "name", u.getName(),
            "email", u.getEmail(),
            "phone", u.getPhone(),
            "address", u.getAddress(),
            "avatarUrl", u.getAvatarUrl()
        )
    ));
  }
}
