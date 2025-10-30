package com.hhplus.ecommerce.coupon.dto;

public record CouponInfo(
        Long couponId,
        String couponName,
        Integer discountAmount
) {
}
