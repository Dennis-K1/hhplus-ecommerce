package com.hhplus.ecommerce.cart.dto;

import java.util.List;

public record CartResponse(
        List<CartItemResponse> items,
        Integer totalAmount
) {
}
