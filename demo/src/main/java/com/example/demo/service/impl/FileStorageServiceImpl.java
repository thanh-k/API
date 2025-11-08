package com.example.demo.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;

@Service
public class FileStorageServiceImpl {

    // Trỏ đến thư mục "uploads" mà bạn vừa tạo
    private final Path root = Paths.get("uploads");

    // Hàm này sẽ được tự động gọi khi ứng dụng khởi động
    @PostConstruct
    public void init() {
        try {
            // Tạo thư mục "uploads" nếu nó chưa tồn tại
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public String save(MultipartFile file) {
        try {
            // Tạo một tên file duy nhất để tránh trùng lặp
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            // Copy file vào thư mục "uploads"
            Files.copy(file.getInputStream(), this.root.resolve(uniqueFilename));

            return uniqueFilename; // Trả về tên file duy nhất đã lưu
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
}