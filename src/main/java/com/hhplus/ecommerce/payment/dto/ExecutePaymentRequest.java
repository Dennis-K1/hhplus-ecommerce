package com.hhplus.ecommerce.payment.dto;

public record ExecutePaymentRequest(
        Long orderId,
        Long userId,
        Long userCouponId
) {
}
