package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity // bật @PreAuthorize
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain chain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable()) // dùng JWT -> disable CSRF cho API
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // ✅ Header chống cache cho các endpoint bảo vệ
            .headers(h -> h.cacheControl(withDefaults()))
            .exceptionHandling(eh -> eh
                .authenticationEntryPoint((req, res, ex) -> {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"error\":\"Unauthorized\"}");
                })
                .accessDeniedHandler((req, res, ex) -> {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"error\":\"Forbidden\"}");
                })
            )
            .authorizeHttpRequests(auth -> auth

                // ===== Cho phép preflight CORS =====
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ===== Files (upload & serve ảnh) =====
                .requestMatchers(HttpMethod.POST, "/files/upload").permitAll()
                .requestMatchers(HttpMethod.GET,  "/files/**").permitAll()

                // ===== Auth public: login / register / forgot password =====
                .requestMatchers(
                    HttpMethod.POST,
                    "/auth/login",
                    "/auth/register",
                    "/auth/forgot/check",
                    "/auth/forgot/reset"
                ).permitAll()

                // ===== Các endpoint auth cần đăng nhập =====
                .requestMatchers(HttpMethod.GET,  "/auth/me").authenticated()
                .requestMatchers(HttpMethod.PUT,  "/auth/profile").authenticated()
                .requestMatchers(HttpMethod.POST, "/auth/change-password").authenticated()

                // ===== Public GET cho dữ liệu sản phẩm & danh mục =====
                .requestMatchers(HttpMethod.GET, "/products/**", "/categories/**").permitAll()

                // ===== Khu admin: ADMIN & STAFF đều vào được (quản lý user, sản phẩm, ...) =====
                .requestMatchers("/admin/**", "/api/admin/**").hasAnyRole("ADMIN", "STAFF")

                // ===== Còn lại yêu cầu authenticated =====
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS cho FE dev
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        // Dùng allowedOriginPatterns để không lỗi khi origin có port
        cfg.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of("Set-Cookie"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception {
        return c.getAuthenticationManager();
    }
}
