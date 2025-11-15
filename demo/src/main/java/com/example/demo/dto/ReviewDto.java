package com.example.demo.dto;

import lombok.*;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {

    private Long id;
    private Integer productId;  // đúng → vì Product ID của bạn là Integer
    private String productName;

    private Long userId;
    private String userName;

    private Integer rating;
    private String comment;

    private Boolean active;
    private LocalDateTime createdAt;
}
