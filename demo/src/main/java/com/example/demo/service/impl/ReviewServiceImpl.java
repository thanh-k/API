package com.example.demo.service.impl;

import com.example.demo.dto.ReviewCreateRequest;
import com.example.demo.dto.ReviewDto;
import com.example.demo.entity.Product;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsByProduct(Integer productId) {
        return reviewRepository
                .findByProductIdAndActiveTrueOrderByCreatedAtDesc(productId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ReviewDto createReview(Integer productId, Long userId, ReviewCreateRequest request) {
        // ⬇️ giờ productId là Integer, khớp với ProductRepository
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 🔒 Chỉ user đã mua mới được review
        boolean hasPurchased = orderDetailRepository
                .hasUserPurchasedProduct(userId, productId);

        if (!hasPurchased) {
            throw new IllegalStateException("User has not purchased this product, cannot review");
        }

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment())
                .active(true)
                .build();

        Review saved = reviewRepository.save(review);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> adminGetAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void adminDeleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new EntityNotFoundException("Review not found");
        }
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public void adminToggleActive(Long reviewId, boolean active) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        review.setActive(active);
        // Hibernate tự flush vì đang trong @Transactional
    }

        private ReviewDto toDto(Review r) {
        return ReviewDto.builder()
                .id(r.getId())
                .productId(r.getProduct().getId())     // getId() = Integer → đúng kiểu
                .productName(r.getProduct().getName())
                .userId(r.getUser().getId())
                .userName(r.getUser().getName())
                .rating(r.getRating())
                .comment(r.getComment())
                .active(r.getActive())
                .createdAt(r.getCreatedAt())
                .build();
    }

}
