package com.example.demo.repository;

import com.example.demo.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Lấy các review đang active của 1 product
    List<Review> findByProductIdAndActiveTrueOrderByCreatedAtDesc(Integer productId);

    // Dùng nếu muốn chặn 1 user review nhiều lần 1 sản phẩm
    boolean existsByProductIdAndUserId(Integer productId, Long userId);
    // (nếu User.id là Integer thì đổi Long → Integer)
}
