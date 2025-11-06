package com.hhplus.ecommerce.presentation.dto;

public record CartItemResponse(
        Long cartItemId,
        ProductInfo product,
        Integer quantity,
        Integer amount
) {
}
