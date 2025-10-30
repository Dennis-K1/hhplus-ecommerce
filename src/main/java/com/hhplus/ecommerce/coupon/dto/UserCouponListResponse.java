package com.hhplus.ecommerce.coupon.dto;

import java.util.List;

public record UserCouponListResponse(
        List<UserCouponResponse> coupons
) {
}
