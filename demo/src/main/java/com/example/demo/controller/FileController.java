package com.example.demo.controller;

import com.example.demo.service.impl.FileStorageServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;



@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageServiceImpl fileStorageService;

    public FileController(FileStorageServiceImpl fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Lưu file và nhận lại tên file duy nhất
            String filename = fileStorageService.save(file);

            // Tạo URL đầy đủ để truy cập file
            // Ví dụ: http://localhost:8080/files/ten-file-duy-nhat.jpg
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/files/")
                    .path(filename)
                    .toUriString();

            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Could not upload the file: " + file.getOriginalFilename() + "!");
        }
    }
}