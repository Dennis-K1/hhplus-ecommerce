package com.hhplus.ecommerce.presentation.dto;

public record OrderItemResponse(
        Long orderItemId,
        Long productId,
        String productName,
        Integer quantity,
        Integer price
) {
}
