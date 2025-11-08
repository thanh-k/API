package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry; // Import mới
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    // --- THÊM PHƯƠNG THỨC NÀY ---
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Khi có yêu cầu tới /files/**, Spring Boot sẽ tìm file trong thư mục "uploads/"
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:./uploads/");
    }
}