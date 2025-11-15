package com.example.demo.repository;

import com.example.demo.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    List<OrderDetail> findByOrderId(Long orderId);

    // Tổng tất cả line price (d.price = đơn giá * số lượng)
    @Query("select coalesce(sum(d.price), 0) from OrderDetail d")
    Double sumAllOrderAmount();

    // nếu cần xoá nhanh chi tiết theo order (ít dùng vì Order đã cascade)
    void deleteByOrderId(Long orderId);
     // Kiểm tra user đã mua 1 product cụ thể chưa
     @Query("""
           select case when count(od) > 0 then true else false end
           from OrderDetail od
           where od.order.user.id = :userId
             and od.product.id = :productId
           """)
    boolean hasUserPurchasedProduct(
            @Param("userId") Long userId,
            @Param("productId") Integer productId
    );
}
