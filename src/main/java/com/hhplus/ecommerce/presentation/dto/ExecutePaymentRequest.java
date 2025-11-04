package com.hhplus.ecommerce.presentation.dto;

public record ExecutePaymentRequest(
        Long orderId,
        Long userId,
        Long userCouponId
) {
}
