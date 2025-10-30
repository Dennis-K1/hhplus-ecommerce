package com.hhplus.ecommerce.order.dto;

public record CreateOrderRequest(
        Long userId,
        String orderType,
        Long productId,
        Integer quantity
) {
}
