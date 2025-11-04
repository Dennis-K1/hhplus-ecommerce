package com.hhplus.ecommerce.presentation.dto;

import java.time.LocalDateTime;

public record AddToCartResponse(
        Long cartItemId,
        Long userId,
        Long productId,
        Integer quantity,
        LocalDateTime createdAt
) {
}
