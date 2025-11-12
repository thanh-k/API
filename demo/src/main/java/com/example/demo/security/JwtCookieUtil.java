package com.example.demo.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

/**
 * Tiện ích quản lý cookie JWT cho môi trường DEV (http://localhost)
 * Không cần HTTPS, dùng SameSite=Lax để tránh chặn cookie.
 */
public final class JwtCookieUtil {
    public static final String COOKIE_NAME = "JWT";
    private static final String COOKIE_PATH = "/";

    private JwtCookieUtil() {}

    /** 
     * Ghi JWT vào cookie HttpOnly để FE không truy cập bằng JS.
     * @param secure dev (HTTP) = false, prod (HTTPS) = true
     * @param maxAgeSeconds thời gian sống (giây)
     */
    public static void writeJwt(HttpServletResponse res, String token, boolean secure, int maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(false)        // ✅ localhost (HTTP): false
                .sameSite("Lax")      // ✅ Cho phép gửi cookie trong request cùng domain (React + Spring)
                .path(COOKIE_PATH)
                .maxAge(maxAgeSeconds)
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /** Xoá cookie JWT */
    public static void clear(HttpServletResponse res, boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path(COOKIE_PATH)
                .maxAge(0)
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /** Đọc JWT từ cookie */
    public static String readJwt(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (COOKIE_NAME.equals(c.getName())) {
                String v = c.getValue();
                return (v == null || v.isEmpty()) ? null : v;
            }
        }
        return null;
    }
}
