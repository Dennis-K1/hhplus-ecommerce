package com.hhplus.ecommerce.presentation.dto;

public record CouponInfo(
        Long couponId,
        String couponName,
        Integer discountAmount
) {
}
