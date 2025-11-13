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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder enc;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    // regex: phone 0 + 9 số
    private static final Pattern PHONE_REGEX = Pattern.compile("^0\\d{9}$");
    // regex email đơn giản: ký tự bất kỳ + @ + domain + . + đuôi
    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public AuthController(UserRepository r, PasswordEncoder e, AuthenticationManager am, JwtService j) {
        this.userRepo = r;
        this.enc = e;
        this.authManager = am;
        this.jwt = j;
    }

    // ========= REGISTER =========
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {

        String email = req.getEmail() != null ? req.getEmail().trim() : "";
        String phone = req.getPhone() != null ? req.getPhone().trim() : "";
        String password = req.getPassword() != null ? req.getPassword() : "";

        // validate email
        if (email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email là bắt buộc"));
        }
        if (!EMAIL_REGEX.matcher(email).matches()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Định dạng email không hợp lệ"));
        }

        // validate phone (nếu có nhập)
        if (!phone.isEmpty() && !PHONE_REGEX.matcher(phone).matches()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Số điện thoại phải gồm 10 số và bắt đầu bằng 0"));
        }

        // validate password
        if (password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mật khẩu phải từ 6 ký tự trở lên"));
        }

        if (userRepo.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email đã được sử dụng"));
        }
        if (!phone.isEmpty() && userRepo.existsByPhone(phone)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Số điện thoại đã được sử dụng"));
        }

        var u = User.builder()
                .name(req.getName())
                .email(email)
                .phone(phone.isEmpty() ? null : phone)
                .address(req.getAddress())
                .password(enc.encode(password))
                .role(User.Role.USER)            // mặc định USER
                .avatarUrl(req.getAvatarUrl())
                .build();

        userRepo.save(u);
        return ResponseEntity.status(201).body(Map.of("message", "Registered successfully"));
    }

    // ========= LOGIN =========
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

        // Ghi JWT vào cookie HttpOnly
        JwtCookieUtil.writeJwt(res, token, false, (int) (expMs / 1000));

        String next = (u.getRole() == User.Role.ADMIN || u.getRole() == User.Role.STAFF) ? "/admin" : "/";

        // Trả kèm role để FE điều hướng/ẩn nút ngay lập tức
        return ResponseEntity.ok(new AuthResponse(token, next, u.getRole().name()));
    }

    // ========= LOGOUT =========
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse res) {
        JwtCookieUtil.clear(res, false);
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    // ========= ME =========
    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        var u = principal.getDomainUser();

        Map<String, Object> body = new HashMap<>();
        body.put("id", u.getId());
        body.put("name", u.getName());
        body.put("email", u.getEmail());
        body.put("role", u.getRole());           // vẫn trả role để FE cross-check
        body.put("avatarUrl", u.getAvatarUrl());
        body.put("phone", u.getPhone());
        body.put("address", u.getAddress());

        // ✅ Thêm header chống cache cho dữ liệu nhạy cảm
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                .header("Pragma", "no-cache")
                .body(body);
    }

    // ========= UPDATE PROFILE =========
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal CustomUserDetails principal,
                                           @RequestBody Map<String, Object> body) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        var u = principal.getDomainUser();

        String name    = (String) body.getOrDefault("name", u.getName());
        String phone   = (String) body.getOrDefault("phone", u.getPhone());
        String address = (String) body.getOrDefault("address", u.getAddress());
        String avatar  = (String) body.get("avatarUrl");

        // validate phone nếu có
        if (phone != null && !phone.isBlank()
                && !PHONE_REGEX.matcher(phone).matches()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Số điện thoại phải gồm 10 số và bắt đầu bằng 0"));
        }

        u.setName(name);
        u.setPhone(phone);
        u.setAddress(address);
        if (avatar != null) {
            u.setAvatarUrl(avatar);
        }

        userRepo.save(u);

        return ResponseEntity.ok(Map.of(
                "message", "Profile updated",
                "user", Map.of(
                        "id", u.getId(),
                        "name", u.getName(),
                        "email", u.getEmail(),
                        "phone", u.getPhone(),
                        "address", u.getAddress(),
                        "avatarUrl", u.getAvatarUrl(),
                        "role", u.getRole()
                )
        ));
    }

    // ========= CHANGE PASSWORD KHI ĐANG ĐĂNG NHẬP =========
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal CustomUserDetails principal,
                                            @RequestBody Map<String, String> req) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        var u = principal.getDomainUser();

        String oldP = req.get("oldPassword");
        String newP = req.get("newPassword");

        if (oldP == null || newP == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Thiếu mật khẩu hiện tại hoặc mật khẩu mới"));
        }

        if (!enc.matches(oldP, u.getPassword())) {
            return ResponseEntity.status(400).body(Map.of("error", "Mật khẩu hiện tại không đúng"));
        }

        if (newP.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mật khẩu mới phải từ 6 ký tự trở lên"));
        }

        u.setPassword(enc.encode(newP));
        userRepo.save(u);

        return ResponseEntity.ok(Map.of("message", "Password changed"));
    }

    // ========= FORGOT PASSWORD – BƯỚC 1: CHECK EMAIL / PHONE =========
    @PostMapping("/forgot/check")
    public ResponseEntity<?> forgotCheck(@RequestBody Map<String, String> body) {
        String identifier = body.get("identifier");

        if (identifier == null || identifier.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Vui lòng nhập email hoặc số điện thoại"));
        }

        identifier = identifier.trim();
        boolean isPhone = PHONE_REGEX.matcher(identifier).matches();
        boolean isEmail = EMAIL_REGEX.matcher(identifier).matches();

        if (!isPhone && !isEmail) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Định dạng email / số điện thoại không hợp lệ"));
        }

        Optional<User> opt = isEmail
                ? userRepo.findByEmail(identifier)
                : userRepo.findByPhone(identifier);

        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Tài khoản không tồn tại"));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Thông tin hợp lệ. Hãy nhập mật khẩu mới."
        ));
    }

    @PostMapping("/forgot/reset")
    public ResponseEntity<?> forgotReset(@RequestBody Map<String, String> body) {
        String identifier = body.get("identifier");
        String newPass = body.get("newPassword");
        String confirm = body.get("confirmPassword");

        // ---- Validate ----
        if (identifier == null || identifier.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Thiếu email hoặc số điện thoại"));
        }

        if (newPass == null || newPass.isBlank() ||
                confirm == null || confirm.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Vui lòng nhập mật khẩu mới và xác nhận mật khẩu"));
        }

        if (!newPass.equals(confirm)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Mật khẩu xác nhận không khớp"));
        }

        if (newPass.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Mật khẩu mới phải ít nhất 6 ký tự"));
        }

        identifier = identifier.trim();
        boolean isPhone = PHONE_REGEX.matcher(identifier).matches();
        boolean isEmail = EMAIL_REGEX.matcher(identifier).matches();

        if (!isPhone && !isEmail) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Định dạng email / số điện thoại không hợp lệ"));
        }

        // ---- Check user ----
        Optional<User> opt = isEmail
                ? userRepo.findByEmail(identifier)
                : userRepo.findByPhone(identifier);

        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Tài khoản không tồn tại"));
        }

        // ---- Update password ----
        User u = opt.get();
        u.setPassword(enc.encode(newPass));
        userRepo.save(u);

        return ResponseEntity.ok(Map.of(
                "message", "Đổi mật khẩu thành công"
        ));
    }

}
