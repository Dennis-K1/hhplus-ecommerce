package com.hhplus.ecommerce.cart.dto;

public record AddToCartRequest(
        Long productId,
        Integer quantity
) {
}
