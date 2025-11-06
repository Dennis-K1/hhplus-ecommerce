package com.hhplus.ecommerce.presentation.dto;

public record AddToCartRequest(
        Long productId,
        Integer quantity
) {
}
