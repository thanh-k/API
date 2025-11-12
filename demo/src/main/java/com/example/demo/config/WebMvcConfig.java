// src/main/java/com/example/demo/config/WebMvcConfig.java
package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private static final Logger log = LoggerFactory.getLogger(WebMvcConfig.class);

  @Value("${app.upload-dir:uploads}")
  private String uploadDir;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
    String location = root.toUri().toString(); // file:/.../uploads/
    log.info("Static files mapping: /api/files/** -> {}", location);

    registry.addResourceHandler("/api/files/**")
            .addResourceLocations(location)
            .setCachePeriod(0);
  }
}
