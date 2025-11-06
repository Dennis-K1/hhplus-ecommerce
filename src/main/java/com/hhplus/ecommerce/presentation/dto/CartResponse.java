package com.hhplus.ecommerce.presentation.dto;

import java.util.List;

public record CartResponse(
        List<CartItemResponse> items,
        Integer totalAmount
) {
}
