package com.example.demo.repository;

import com.example.demo.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    List<OrderDetail> findByOrderId(Long orderId);

    // Tổng tất cả line price (d.price = đơn giá * số lượng)
    @Query("select coalesce(sum(d.price), 0) from OrderDetail d")
    Double sumAllOrderAmount();

    // nếu cần xoá nhanh chi tiết theo order (ít dùng vì Order đã cascade)
    void deleteByOrderId(Long orderId);
}
