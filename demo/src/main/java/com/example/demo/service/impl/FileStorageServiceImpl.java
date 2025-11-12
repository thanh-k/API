// src/main/java/com/example/demo/service/impl/FileStorageServiceImpl.java
package com.example.demo.service.impl;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageServiceImpl {

    private final Path root = Paths.get("uploads").toAbsolutePath().normalize();

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!", e);
        }
    }

    /** Lưu file và trả về TÊN FILE đã lưu (không kèm đường dẫn) */
    public String save(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) throw new IOException("Empty file");

            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null) {
                int dot = original.lastIndexOf('.');
                if (dot >= 0) ext = original.substring(dot); // ".jpg" | ".png" | ""
            }

            String unique = UUID.randomUUID().toString().replace("-", "") + ext;

            Files.copy(
                file.getInputStream(),
                this.root.resolve(unique),
                StandardCopyOption.REPLACE_EXISTING
            );
            return unique;
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage(), e);
        }
    }
}
