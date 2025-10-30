package com.hhplus.ecommerce.cart.dto;

public record ProductInfo(
        Long productId,
        String productName,
        Integer price
) {
}
