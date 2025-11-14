package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminDashboardDto {

    private double totalRevenue;      // Tổng doanh thu (tổng price ở OrderDetail)
    private long totalUsers;          // Tổng user (role = USER)
    private long totalProducts;       // Tổng sản phẩm
    private long totalCategories;     // Tổng danh mục

    // Các đơn hàng mới trong 2 ngày gần nhất (dùng lại OrderDto đã có)
    private List<OrderDto> recentOrders;
}
