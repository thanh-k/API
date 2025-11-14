package com.example.demo.controller;

import com.example.demo.dto.AdminUserRequest;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final OrderRepository orderRepo;
    private final OrderDetailRepository orderDetailRepo;

    private ResponseEntity<?> bad(String msg) {
        return ResponseEntity.badRequest().body(Map.of("error", msg));
    }

    /* ===== helper: Entity -> DTO ===== */
    private UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .address(u.getAddress())
                .role(u.getRole() != null ? u.getRole().name() : null)
                .avatarUrl(u.getAvatarUrl())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }

    /* ===== READ all ===== */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> listAll() {
        return userRepo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /* ===== READ by id ===== */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<User> opt = userRepo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User không tồn tại"));
        }
        return ResponseEntity.ok(toDto(opt.get()));
    }

    /* ===== CREATE ===== */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody AdminUserRequest req) {

        // Email + password bắt buộc
        if (req.getEmail() == null || req.getEmail().isBlank()
                || req.getPassword() == null || req.getPassword().isBlank()) {
            return bad("Email và mật khẩu là bắt buộc");
        }

        // Validate email format
        if (!ValidationUtil.isValidEmail(req.getEmail())) {
            return bad("Email không hợp lệ (ví dụ: ten@domain.com)");
        }

        // Validate phone format nếu có
        if (req.getPhone() != null && !req.getPhone().isBlank()
                && !ValidationUtil.isValidPhone(req.getPhone())) {
            return bad("Số điện thoại không hợp lệ (phải gồm 10 chữ số và bắt đầu bằng 0)");
        }

        // Password length
        if (!ValidationUtil.isValidPassword(req.getPassword())) {
            return bad("Mật khẩu phải có ít nhất 6 ký tự");
        }

        // trùng email
        if (userRepo.existsByEmail(req.getEmail())) {
            return bad("Email đã được sử dụng bởi tài khoản khác");
        }

        // trùng phone
        if (req.getPhone() != null && !req.getPhone().isBlank()
                && userRepo.existsByPhone(req.getPhone())) {
            return bad("Số điện thoại đã được sử dụng bởi tài khoản khác");
        }

        User u = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .address(req.getAddress())
                .avatarUrl(req.getAvatarUrl())
                .password(encoder.encode(req.getPassword()))
                .role(req.getRole() != null ? req.getRole() : User.Role.USER)
                .build();

        userRepo.save(u);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(u));
    }

    /* ===== UPDATE ===== */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody AdminUserRequest req) {

        Optional<User> opt = userRepo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User không tồn tại"));
        }
        User u = opt.get();

        // ==== EMAIL ====
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            if (!ValidationUtil.isValidEmail(req.getEmail())) {
                return bad("Email không hợp lệ (ví dụ: ten@domain.com)");
            }
            if (!req.getEmail().equalsIgnoreCase(u.getEmail())
                    && userRepo.existsByEmail(req.getEmail())) {
                return bad("Email đã được sử dụng bởi tài khoản khác");
            }
            u.setEmail(req.getEmail());
        }

        // ==== PHONE ====
        if (req.getPhone() != null && !req.getPhone().isBlank()) {
            if (!ValidationUtil.isValidPhone(req.getPhone())) {
                return bad("Số điện thoại không hợp lệ (phải gồm 10 chữ số và bắt đầu bằng 0)");
            }
            if (!req.getPhone().equals(u.getPhone())
                    && userRepo.existsByPhone(req.getPhone())) {
                return bad("Số điện thoại đã được sử dụng bởi tài khoản khác");
            }
            u.setPhone(req.getPhone());
        }

        // ==== PASSWORD (tùy chọn) ====
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            if (!ValidationUtil.isValidPassword(req.getPassword())) {
                return bad("Mật khẩu mới phải có ít nhất 6 ký tự");
            }
            u.setPassword(encoder.encode(req.getPassword()));
        }

        // ==== field khác ====
        if (req.getName() != null)       u.setName(req.getName());
        if (req.getAddress() != null)    u.setAddress(req.getAddress());
        if (req.getAvatarUrl() != null)  u.setAvatarUrl(req.getAvatarUrl());
        if (req.getRole() != null)       u.setRole(req.getRole());

        userRepo.save(u);
        return ResponseEntity.ok(toDto(u));
    }

    /* ===== DELETE (có xử lý đơn hàng) ===== */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            @RequestParam(name = "force", required = false, defaultValue = "false") boolean forceDelete,
            @RequestParam(name = "removeOrders", required = false, defaultValue = "false") boolean removeOrders
    ) {
        Optional<User> opt = userRepo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User không tồn tại"));
        }

        User user = opt.get();

        long orderCount = orderRepo.countByUserId(id);

        // Trường hợp có đơn hàng nhưng FE không gửi force
        if (orderCount > 0 && !forceDelete) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error", "USER_HAS_ORDERS",
                            "message", "User đã có đơn hàng, không thể xóa trực tiếp.",
                            "orderCount", orderCount,
                            "hasOrders", true 
                    ));
        }

        // FORCE DELETE: xóa user + toàn bộ đơn hàng
        if (forceDelete) {
            List<Order> orders = orderRepo.findByUserId(id);
            orders.forEach(o ->
                    orderDetailRepo.deleteAll(orderDetailRepo.findByOrderId(o.getId()))
            );
            orderRepo.deleteAll(orders);

            userRepo.delete(user);
            return ResponseEntity.ok(Map.of(
                    "message", "Đã xóa user và toàn bộ đơn hàng.",
                    "orderDeleted", orders.size()
            ));
        }

        // Trường hợp không có đơn → xóa bình thường
        try {
            userRepo.delete(user);
            return ResponseEntity.ok(Map.of("message", "Đã xoá user thành công."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể xoá user."));
        }
    }
}
