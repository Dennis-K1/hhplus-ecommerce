package com.hhplus.ecommerce.presentation.dto;

import java.time.LocalDateTime;

public record OrderListItemResponse(
        Long orderId,
        String orderStatus,
        Integer totalAmount,
        Integer discountAmount,
        Integer finalAmount,
        LocalDateTime createdAt
) {
}
