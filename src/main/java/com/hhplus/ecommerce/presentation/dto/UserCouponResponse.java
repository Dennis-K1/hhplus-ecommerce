package com.hhplus.ecommerce.presentation.dto;

import java.time.LocalDateTime;

public record UserCouponResponse(
        Long userCouponId,
        CouponInfo coupon,
        Boolean isUsed,
        LocalDateTime issuedAt,
        LocalDateTime expiredAt
) {
}
