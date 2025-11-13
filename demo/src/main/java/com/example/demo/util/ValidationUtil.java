package com.example.demo.util;

import java.util.regex.Pattern;

public final class ValidationUtil {

    // 10 số, bắt đầu bằng 0  →  ví dụ: 0901234567
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9}$");

    // email: ký tự đầu là chữ/số, sau đó chữ/số/._%+-, tiếp theo @domain và .com/.com.vn/...
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9][A-Za-z0-9._%+-]*@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationUtil() {}

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    // password ≥ 6 ký tự
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}
