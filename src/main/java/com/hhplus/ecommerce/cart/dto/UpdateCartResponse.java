package com.hhplus.ecommerce.cart.dto;

import java.time.LocalDateTime;

public record UpdateCartResponse(
        Long cartItemId,
        Long userId,
        Long productId,
        Integer quantity,
        LocalDateTime updatedAt
) {
}
