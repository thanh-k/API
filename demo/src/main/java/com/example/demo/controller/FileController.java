// src/main/java/com/example/demo/controller/FileController.java
package com.example.demo.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileController {

  // thư mục lưu file (có thể đưa vào application.properties: app.upload-dir=uploads)
  private final Path uploadRoot = Paths.get("uploads");

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                  HttpServletRequest req) throws Exception {
    if (file == null || file.isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("error", "Empty file"));
    }

    // Tạo thư mục theo ngày cho gọn
    Path dir = uploadRoot.resolve(LocalDate.now().toString());
    if (!Files.exists(dir)) Files.createDirectories(dir);

    String clean = StringUtils.cleanPath(file.getOriginalFilename());
    String filename = System.currentTimeMillis() + "_" + clean;
    Path target = dir.resolve(filename);

    // Ghi file
    Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

    // Build URL (tôn trọng context-path /api)
    String ctx = req.getContextPath();               // ví dụ: "/api" hoặc ""
    if (ctx == null) ctx = "";
    String url = ctx + "/files/" + LocalDate.now() + "/" + filename;  // => /api/files/2025-11-13/xxx.png

    // Trả về CHUỖI URL để FE gán thẳng imageUrl
    return ResponseEntity.status(HttpStatus.CREATED).body(url);
  }

  // Serve file lại qua GET /api/files/{date}/{filename}
  @GetMapping("/{date}/{filename:.+}")
  public ResponseEntity<Resource> serve(@PathVariable String date,
                                        @PathVariable String filename) throws MalformedURLException {
    Path file = uploadRoot.resolve(date).resolve(filename).normalize();
    if (!Files.exists(file)) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    Resource resource = new UrlResource(file.toUri());
    MediaType type = MediaType.APPLICATION_OCTET_STREAM;
    try {
      String guess = Files.probeContentType(file);
      if (guess != null) type = MediaType.parseMediaType(guess);
    } catch (Exception ignored) { }

    return ResponseEntity.ok()
        .contentType(type)
        .cacheControl(CacheControl.noCache())
        .body(resource);
  }
}
