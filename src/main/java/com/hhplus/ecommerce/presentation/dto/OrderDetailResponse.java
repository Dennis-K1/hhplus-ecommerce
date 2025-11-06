package com.hhplus.ecommerce.presentation.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
        Long orderId,
        Long userId,
        String orderStatus,
        List<OrderItemResponse> items,
        Integer totalAmount,
        Integer discountAmount,
        Long usedCouponId,
        Integer finalAmount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
