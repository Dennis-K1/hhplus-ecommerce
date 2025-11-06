package com.hhplus.ecommerce.presentation.dto;

import java.util.List;

public record UserCouponListResponse(
        List<UserCouponResponse> coupons
) {
}
