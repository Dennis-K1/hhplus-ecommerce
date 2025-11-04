package com.hhplus.ecommerce.presentation.dto;

public record CreateOrderRequest(
        Long userId,
        String orderType,
        Long productId,
        Integer quantity
) {
}
