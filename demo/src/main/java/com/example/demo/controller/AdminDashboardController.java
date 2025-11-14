package com.example.demo.controller;
import com.example.demo.entity.User; 
import com.example.demo.dto.AdminDashboardDto;
import com.example.demo.dto.OrderDto;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.Product;
import com.example.demo.repository.*;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.RoleUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final OrderRepository orderRepo;
    private final OrderDetailRepository detailRepo;

    // ===== Helper: map OrderDetail -> OrderDto.Item =====
    private OrderDto.Item toItemDto(OrderDetail d) {
        Product p = d.getProduct();

        return OrderDto.Item.builder()
                .id(d.getId())
                .productId(p.getId())
                .productName(p.getName())
                .quantity(d.getQuantity())
                .price(d.getPrice())   // line total (giá * số lượng)
                .build();
    }

    private OrderDto toOrderDto(Order o, List<OrderDetail> details) {
        var items = details.stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());

        double total = items.stream()
                .mapToDouble(i -> Optional.ofNullable(i.getPrice()).orElse(0.0))
                .sum();

        return OrderDto.builder()
                .id(o.getId())
                .userId(o.getUser().getId())
                .name(o.getName())
                .phone(o.getPhone())
                .email(o.getEmail())
                .address(o.getAddress())
                .createdAt(o.getCreatedAt())
                .totalAmount(total)
                .items(items)
                .build();
    }

    // ===== GET /api/admin/dashboard =====
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<?> getDashboard(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        var user = principal.getDomainUser();
        boolean isAdmin = RoleUtils.isAdmin(user);

        // ✅ DÙNG ENUM THEO REPO MỚI
        long totalUsers = userRepo.countByRole(User.Role.USER);

        long totalProducts   = productRepo.count();
        long totalCategories = categoryRepo.count();

        Double revenue = detailRepo.sumAllOrderAmount();
        if (revenue == null) revenue = 0.0;

        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        List<Order> recentOrders =
                orderRepo.findByCreatedAtAfterOrderByCreatedAtDesc(twoDaysAgo);

        List<OrderDto> recentDtos = recentOrders.stream()
                .map(o -> {
                    List<OrderDetail> ds = detailRepo.findByOrderId(o.getId());
                    return toOrderDto(o, ds);
                })
                .collect(Collectors.toList());

        AdminDashboardDto full = AdminDashboardDto.builder()
                .totalRevenue(revenue)
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .totalCategories(totalCategories)
                .recentOrders(recentDtos)
                .build();

        if (!isAdmin) {
            AdminDashboardDto staffView = AdminDashboardDto.builder()
                    .totalUsers(totalUsers)
                    .totalProducts(totalProducts)
                    .totalCategories(totalCategories)
                    .recentOrders(recentDtos)
                    .build();
            return ResponseEntity.ok(staffView);
        }

        return ResponseEntity.ok(full);
    }
}

