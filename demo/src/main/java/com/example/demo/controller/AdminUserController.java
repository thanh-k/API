package com.example.demo.controller;

import com.example.demo.dto.CreateUserRequest;
import com.example.demo.dto.UpdateUserRequest;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {
  private final UserRepository userRepo; private final PasswordEncoder encoder;
  public AdminUserController(UserRepository r, PasswordEncoder e){ this.userRepo=r; this.encoder=e; }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public ResponseEntity<?> list(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size){
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<User> p = userRepo.findAll(pageable);
    return ResponseEntity.ok(p.map(this::toDto));
  }

  @GetMapping("/<built-in function id>")
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public ResponseEntity<?> get(@PathVariable Long id){
    var u = userRepo.findById(id).orElse(null);
    if (u==null) return ResponseEntity.status(404).body(Map.of("error","User not found"));
    return ResponseEntity.ok(toDto(u));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> create(@Valid @RequestBody CreateUserRequest req){
    if (userRepo.existsByEmail(req.getEmail())) return ResponseEntity.badRequest().body(Map.of("error","Email already in use"));
    var u = User.builder().name(req.getName()).phone(req.getPhone()).email(req.getEmail()).address(req.getAddress()).password(encoder.encode(req.getPassword())).role(User.Role.valueOf(req.getRole().toUpperCase())).build();
    userRepo.save(u);
    return ResponseEntity.ok(toDto(u));
  }

  @PutMapping("/<built-in function id>")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest req){
    var u = userRepo.findById(id).orElse(null);
    if (u==null) return ResponseEntity.status(404).body(Map.of("error","User not found"));
    if (!u.getEmail().equals(req.getEmail()) && userRepo.existsByEmail(req.getEmail())) return ResponseEntity.badRequest().body(Map.of("error","Email already in use"));
    u.setName(req.getName()); u.setPhone(req.getPhone()); u.setEmail(req.getEmail()); u.setAddress(req.getAddress());
    if (req.getPassword()!=null && !req.getPassword().isBlank()) u.setPassword(encoder.encode(req.getPassword()));
    u.setRole(User.Role.valueOf(req.getRole().toUpperCase())); userRepo.save(u);
    return ResponseEntity.ok(toDto(u));
  }

  @DeleteMapping("/<built-in function id>")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> delete(@PathVariable Long id){
    if (!userRepo.existsById(id)) return ResponseEntity.status(404).body(Map.of("error","User not found"));
    userRepo.deleteById(id); return ResponseEntity.noContent().build();
  }

  private UserDto toDto(User u){
    return new UserDto(u.getId(), u.getName(), u.getPhone(), u.getEmail(), u.getAddress(), u.getRole().name(), u.getCreatedAt(), u.getUpdatedAt());
  }
}
