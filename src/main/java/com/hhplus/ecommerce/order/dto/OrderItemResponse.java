package com.hhplus.ecommerce.order.dto;

public record OrderItemResponse(
        Long orderItemId,
        Long productId,
        String productName,
        Integer quantity,
        Integer price
) {
}
