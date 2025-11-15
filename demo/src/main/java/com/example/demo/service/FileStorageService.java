package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadRoot = Paths.get("uploads").toAbsolutePath().normalize();

    public FileStorageService() {
        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload folder", e);
        }
    }

    /**
     * Lưu file vào uploads/yyyy-MM-dd/<uuid>_<cleanName>
     * Trả về đường dẫn tương đối phục vụ FE: /files/yyyy-MM-dd/<filename>
     */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + "_" + original.replaceAll("\\s+", "_");
        filename = filename.replace("..", "");

        String dateFolder = LocalDate.now().toString(); // yyyy-MM-dd
        Path dir = uploadRoot.resolve(dateFolder);
        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }

        // NOTE: FileController serves files under /files/{date}/{filename}
        return "/files/" + dateFolder + "/" + filename;
    }

    /**
     * Xóa file nếu cần (đầu vào là imageUrl như /files/yyyy-mm-dd/name)
     */
    public void deleteByImageUrl(String imageUrl) {
        if (imageUrl == null) return;
        String prefix = "/files/";
        int pos = imageUrl.indexOf(prefix);
        if (pos >= 0) {
            String relative = imageUrl.substring(pos + prefix.length());
            Path path = uploadRoot.resolve(relative).normalize();
            try {
                Files.deleteIfExists(path);
            } catch (Exception ignored) {}
        }
    }
}
