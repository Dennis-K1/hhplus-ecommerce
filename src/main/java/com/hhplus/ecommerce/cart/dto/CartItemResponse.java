package com.hhplus.ecommerce.cart.dto;

public record CartItemResponse(
        Long cartItemId,
        ProductInfo product,
        Integer quantity,
        Integer amount
) {
}
