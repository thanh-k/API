package com.example.demo.repository;

import com.example.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // lịch sử đơn theo user
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    // dùng để xoá tất cả đơn của 1 user
    List<Order> findByUserId(Long userId);

    // kiểm tra user đã từng có đơn chưa
    long countByUserId(Long userId);

    // lấy đơn tạo sau 1 thời điểm (dùng cho dashboard "đơn mới")
    List<Order> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime createdAt);
}
