package com.example.demo.controller;

import com.example.demo.dto.ReviewDto;
import com.example.demo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    private final ReviewService reviewService;

    // GET /api/admin/reviews  → admin xem tất cả review
    @GetMapping
    public List<ReviewDto> getAll() {
        return reviewService.adminGetAllReviews();
    }

    // DELETE /api/admin/reviews/{id}  → xoá review
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reviewService.adminDeleteReview(id);
    }

    // PATCH /api/admin/reviews/{id}/active?value=false  → ẩn/hiện review
    @PatchMapping("/{id}/active")
    public void toggleActive(
            @PathVariable Long id,
            @RequestParam("value") boolean active
    ) {
        reviewService.adminToggleActive(id, active);
    }
}
