package com.example.demo.controller;

import com.example.demo.dto.OrderCreateDto;
import com.example.demo.dto.OrderDto;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepo;
    private final OrderDetailRepository detailRepo;
    private final ProductRepository productRepo;

    /* ===== Helper: map Entity -> DTO ===== */

    private OrderDto.Item toItemDto(OrderDetail d) {
        Product p = d.getProduct();

        return OrderDto.Item.builder()
                .id(d.getId())
                // p.getId() là Integer -> productId trong DTO cũng Integer
                .productId(p != null ? p.getId() : null)
                .productName(p != null ? p.getName() : null)
                .quantity(d.getQuantity())
                .price(d.getPrice())       // line total
                .build();
    }

    private OrderDto toOrderDto(Order o, List<OrderDetail> details) {
        List<OrderDto.Item> items = details.stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());

        double total = items.stream()
                .mapToDouble(i -> Optional.ofNullable(i.getPrice()).orElse(0.0))
                .sum();

        return OrderDto.builder()
                .id(o.getId())
                .userId(o.getUser() != null ? o.getUser().getId() : null)
                .name(o.getName())
                .phone(o.getPhone())
                .email(o.getEmail())
                .address(o.getAddress())
                .createdAt(o.getCreatedAt())
                .totalAmount(total)
                .items(items)
                .build();
    }

    /* ===== POST /api/orders – user tạo đơn ===== */
    @PostMapping
    public ResponseEntity<?> createOrder(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody OrderCreateDto req) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }

        User user = principal.getDomainUser();

        if (req.getItems() == null || req.getItems().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Giỏ hàng trống, không thể tạo đơn"));
        }

        if (req.getName() == null || req.getName().isBlank()
                || req.getPhone() == null || req.getPhone().isBlank()
                || req.getEmail() == null || req.getEmail().isBlank()
                || req.getAddress() == null || req.getAddress().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Vui lòng nhập đầy đủ thông tin nhận hàng"));
        }

        Order order = Order.builder()
                .user(user)
                .name(req.getName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .address(req.getAddress())
                .build();

        order = orderRepo.save(order); // có id & createdAt

        List<OrderDetail> details = new ArrayList<>();

        for (OrderCreateDto.Item itemReq : req.getItems()) {
            if (itemReq.getProductId() == null ||
                itemReq.getQuantity() == null ||
                itemReq.getQuantity() <= 0) {
                continue;
            }

            // productRepo là CrudRepository<Product, Integer>
            Product p = productRepo.findById(itemReq.getProductId()).orElse(null);
            if (p == null) continue;

            double unitPrice = Optional.ofNullable(p.getPrice()).orElse(0.0);
            double linePrice = unitPrice * itemReq.getQuantity();

            OrderDetail d = OrderDetail.builder()
                    .order(order)
                    .product(p)
                    .quantity(itemReq.getQuantity())
                    .price(linePrice)
                    .build();

            details.add(d);
        }

        if (details.isEmpty()) {
            orderRepo.delete(order);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Không có sản phẩm hợp lệ trong đơn hàng"));
        }

        detailRepo.saveAll(details);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toOrderDto(order, details));
    }

    /* ===== GET /api/orders/my-orders – lịch sử đơn của user ===== */
    @GetMapping("/my-orders")
    public ResponseEntity<?> myOrders(@AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }

        Long userId = principal.getDomainUser().getId();
        List<Order> orders = orderRepo.findByUserIdOrderByCreatedAtDesc(userId);

        List<OrderDto> result = orders.stream()
                .map(o -> {
                    List<OrderDetail> details = detailRepo.findByOrderId(o.getId());
                    return toOrderDto(o, details);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /* ===== GET /api/orders – admin xem tất cả đơn ===== */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<?> listAllOrders() {
        List<Order> orders = orderRepo.findAll();
        orders.sort(Comparator.comparing(Order::getCreatedAt).reversed());

        List<OrderDto> result = orders.stream()
                .map(o -> {
                    List<OrderDetail> details = detailRepo.findByOrderId(o.getId());
                    return toOrderDto(o, details);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /* ===== GET /api/orders/{id} – admin xem chi tiết 1 đơn ===== */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        Optional<Order> opt = orderRepo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Đơn hàng không tồn tại"));
        }
        Order o = opt.get();
        List<OrderDetail> details = detailRepo.findByOrderId(o.getId());
        return ResponseEntity.ok(toOrderDto(o, details));
    }
}
