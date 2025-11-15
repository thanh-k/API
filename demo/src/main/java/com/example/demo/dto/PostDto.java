package com.example.demo.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private Integer  id;
    private String title;
    private String slug;
    private String content;
    private String imageUrl; // full path returned by FileStorageService e.g. /files/2025-11-15/xxx.png
    private Integer  topicId;
    private String topicName;
    private LocalDateTime createdAt;
}
