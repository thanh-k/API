package com.example.demo.service;

import com.example.demo.dto.ReviewCreateRequest;
import com.example.demo.dto.ReviewDto;

import java.util.List;

public interface ReviewService {

    // User/public
    List<ReviewDto> getReviewsByProduct(Integer productId);

    ReviewDto createReview(Integer productId, Long userId, ReviewCreateRequest request);
    // (userId để Long, nếu User.id là Integer thì đổi thành Integer luôn cũng được)

    // Admin
    List<ReviewDto> adminGetAllReviews();

    void adminDeleteReview(Long reviewId);

    void adminToggleActive(Long reviewId, boolean active);
}
