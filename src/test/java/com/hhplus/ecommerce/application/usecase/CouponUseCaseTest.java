package com.hhplus.ecommerce.application.usecase;

import com.hhplus.ecommerce.domain.entity.Coupon;
import com.hhplus.ecommerce.domain.entity.UserCoupon;
import com.hhplus.ecommerce.domain.exception.CouponNotFoundException;
import com.hhplus.ecommerce.domain.exception.CouponSoldOutException;
import com.hhplus.ecommerce.domain.exception.CouponNotIssuablePeriodException;
import com.hhplus.ecommerce.mock.MockCouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CouponUseCase 단위 테스트
 * - Mock 구현체를 사용한 순수 단위 테스트
 * - Mockito 없이 실제 동작 검증
 */
class CouponUseCaseTest {

    private MockCouponRepository couponRepository;
    private CouponUseCase couponUseCase;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        couponRepository = new MockCouponRepository();
        couponUseCase = new CouponUseCase(couponRepository);
        now = LocalDateTime.now();
    }

    @Test
    @DisplayName("사용자 쿠폰 목록 조회 성공")
    void getUserCoupons_성공() {
        // Given
        Long userId = 1L;
        Coupon coupon = new Coupon(1L, 5000, 10, 1, now.minusDays(1), now.plusDays(30));
        couponRepository.saveCoupon(coupon);

        UserCoupon userCoupon = new UserCoupon(1L, userId, 1L, 0, false, now, null, now.plusDays(30));
        couponRepository.saveUserCoupon(userCoupon);

        // When
        List<UserCoupon> result = couponUseCase.getUserCoupons(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCouponId());
    }

    @Test
    @DisplayName("쿠폰 발급 성공")
    void issueCoupon_성공() {
        // Given
        Long userId = 1L;
        Long couponId = 1L;
        Coupon coupon = new Coupon(couponId, 5000, 10, 0, now.minusDays(1), now.plusDays(30));
        couponRepository.saveCoupon(coupon);

        // When
        UserCoupon result = couponUseCase.issueCoupon(userId, couponId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(couponId, result.getCouponId());
        assertFalse(result.isUsed());

        // 쿠폰 발급 수량 증가 확인
        Coupon updatedCoupon = couponRepository.findById(couponId).orElseThrow();
        assertEquals(1, updatedCoupon.getIssuedQuantity());
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 쿠폰 없음")
    void issueCoupon_쿠폰없음() {
        // Given
        Long userId = 1L;
        Long couponId = 999L;

        // When & Then
        assertThrows(CouponNotFoundException.class, () -> {
            couponUseCase.issueCoupon(userId, couponId);
        });
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 발급 수량 초과")
    void issueCoupon_수량초과() {
        // Given
        Long userId = 1L;
        Long couponId = 1L;
        Coupon soldOutCoupon = new Coupon(couponId, 5000, 10, 10, now.minusDays(1), now.plusDays(30));
        couponRepository.saveCoupon(soldOutCoupon);

        // When & Then
        assertThrows(CouponSoldOutException.class, () -> {
            couponUseCase.issueCoupon(userId, couponId);
        });
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 발급 기간 이전")
    void issueCoupon_발급기간이전() {
        // Given
        Long userId = 1L;
        Long couponId = 1L;
        Coupon notStartedCoupon = new Coupon(couponId, 5000, 10, 0,
                now.plusDays(1), now.plusDays(30));
        couponRepository.saveCoupon(notStartedCoupon);

        // When & Then
        assertThrows(CouponNotIssuablePeriodException.class, () -> {
            couponUseCase.issueCoupon(userId, couponId);
        });
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 발급 기간 만료")
    void issueCoupon_발급기간만료() {
        // Given
        Long userId = 1L;
        Long couponId = 1L;
        Coupon expiredCoupon = new Coupon(couponId, 5000, 10, 0,
                now.minusDays(30), now.minusDays(1));
        couponRepository.saveCoupon(expiredCoupon);

        // When & Then
        assertThrows(CouponNotIssuablePeriodException.class, () -> {
            couponUseCase.issueCoupon(userId, couponId);
        });
    }
}
