package com.hhplus.ecommerce.order.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long orderId,
        Long userId,
        String orderStatus,
        List<OrderItemResponse> items,
        Integer totalAmount,
        Integer discountAmount,
        LocalDateTime createdAt
) {
}
