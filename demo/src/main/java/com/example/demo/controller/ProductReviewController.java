package com.example.demo.controller;

import com.example.demo.dto.ReviewCreateRequest;
import com.example.demo.dto.ReviewDto;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products/{productId}/reviews")
public class ProductReviewController {

    private final ReviewService reviewService;

    // GET /api/products/{id}/reviews
    @GetMapping
    public List<ReviewDto> getReviews(@PathVariable Integer productId) {
        return reviewService.getReviewsByProduct(productId);
    }

    // POST /api/products/{id}/reviews
    @PostMapping
    public ReviewDto createReview(
            @PathVariable Integer productId,
            @Valid @RequestBody ReviewCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Long userId = currentUser.getDomainUser().getId(); // giống UserSelfController của bạn
        return reviewService.createReview(productId, userId, request);
    }
}
