package com.example.demo.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostFormDto {
    private String title;
    private String slug;
    private String content;
    private MultipartFile file; // image file
    private Integer  topicId;
}
