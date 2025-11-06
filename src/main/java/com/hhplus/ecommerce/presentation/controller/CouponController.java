package com.hhplus.ecommerce.presentation.controller;

import com.hhplus.ecommerce.application.usecase.CouponUseCase;
import com.hhplus.ecommerce.common.ApiResponse;
import com.hhplus.ecommerce.domain.entity.UserCoupon;
import com.hhplus.ecommerce.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Coupon", description = "쿠폰 API")
@RestController
@RequestMapping("/api/users/{userId}/coupons")
public class CouponController {

    private final CouponUseCase couponUseCase;

    public CouponController(CouponUseCase couponUseCase) {
        this.couponUseCase = couponUseCase;
    }

    @Operation(summary = "사용자 쿠폰 목록 조회", description = "사용자가 보유한 쿠폰 목록을 조회합니다")
    @GetMapping
    public ApiResponse<UserCouponListResponse> getUserCoupons(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long userId) {

        List<UserCoupon> userCoupons = couponUseCase.getUserCoupons(userId);

        // Response 변환 (임시로 빈 리스트 사용 - 실제로는 Coupon 정보를 가져와야 함)
        List<UserCouponResponse> coupons = Arrays.asList();

        UserCouponListResponse data = new UserCouponListResponse(coupons);
        return ApiResponse.success(data);
    }

    @Operation(summary = "쿠폰 발급", description = "사용자에게 쿠폰을 발급합니다")
    @PostMapping
    public ApiResponse<UserCouponResponse> issueCoupon(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long userId,
            @RequestBody IssueCouponRequest request) {

        UserCoupon userCoupon = couponUseCase.issueCoupon(userId, request.couponId());

        // Response 변환 (임시 - 실제로는 Coupon 정보를 가져와야 함)
        UserCouponResponse data = new UserCouponResponse(
                userCoupon.getId(),
                new CouponInfo(userCoupon.getCouponId(), "할인쿠폰", 10000),
                userCoupon.isUsed(),
                userCoupon.getIssuedAt(),
                userCoupon.getExpiredAt()
        );

        return ApiResponse.success(data, "쿠폰이 발급되었습니다");
    }
}
