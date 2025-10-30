package com.hhplus.ecommerce.coupon;

import com.hhplus.ecommerce.common.ApiResponse;
import com.hhplus.ecommerce.coupon.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Tag(name = "Coupon", description = "쿠폰 API")
@RestController
@RequestMapping("/api/users/{userId}/coupons")
public class CouponController {

    @Operation(summary = "사용자 쿠폰 목록 조회", description = "사용자가 보유한 쿠폰 목록을 조회합니다")
    @GetMapping
    public ApiResponse<UserCouponListResponse> getUserCoupons(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long userId) {
        List<UserCouponResponse> coupons = Arrays.asList(
                new UserCouponResponse(
                        1L,
                        new CouponInfo(5L, "신규가입 할인쿠폰", 10000),
                        false,
                        LocalDateTime.of(2025, 1, 1, 0, 0),
                        LocalDateTime.of(2025, 12, 31, 23, 59, 59)
                )
        );

        UserCouponListResponse data = new UserCouponListResponse(coupons);
        return ApiResponse.success(data);
    }

    @Operation(summary = "쿠폰 발급", description = "사용자에게 쿠폰을 발급합니다")
    @PostMapping
    public ApiResponse<UserCouponResponse> issueCoupon(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long userId,
            @RequestBody IssueCouponRequest request) {

        UserCouponResponse data = new UserCouponResponse(
                1L,
                new CouponInfo(request.couponId(), "신규가입 할인쿠폰", 10000),
                false,
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 12, 31, 23, 59, 59)
        );

        return ApiResponse.success(data, "쿠폰이 발급되었습니다");
    }
}
