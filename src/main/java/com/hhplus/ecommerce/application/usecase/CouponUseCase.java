package com.hhplus.ecommerce.application.usecase;

import com.hhplus.ecommerce.domain.entity.Coupon;
import com.hhplus.ecommerce.domain.entity.UserCoupon;
import com.hhplus.ecommerce.domain.exception.CouponNotFoundException;
import com.hhplus.ecommerce.domain.lock.CouponLock;
import com.hhplus.ecommerce.domain.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 쿠폰 유스케이스
 * - 쿠폰 조회/발급 비즈니스 로직
 * - Mock 환경에서 synchronized로 동시성 제어
 * - 실제 프로덕션에서는 @Transactional + JPA 비관적 락 사용 필요
 */
@Service
public class CouponUseCase {

    private final CouponRepository couponRepository;

    public CouponUseCase(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    /**
     * 사용자 쿠폰 목록 조회
     */
    public List<UserCoupon> getUserCoupons(Long userId) {
        return couponRepository.findByUserId(userId);
    }

    /**
     * 쿠폰 발급
     * - synchronized로 동시성 제어 (Mock 테스트용)
     * - 실제 프로덕션에서는 @Transactional + JPA 비관적 락으로 교체 필요
     */
    @CouponLock
    public UserCoupon issueCoupon(Long userId, Long couponId) {
        // 쿠폰 조회
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(CouponNotFoundException::new);

        // 쿠폰 발급 처리 (도메인 로직)
        LocalDateTime now = LocalDateTime.now();
        coupon.issue(now);
        couponRepository.saveCoupon(coupon);

        // 사용자 쿠폰 생성
        UserCoupon userCoupon = new UserCoupon(
                generateUserCouponId(),
                userId,
                couponId,
                0,  // orderId는 나중에 사용 시 설정
                false,
                now,
                null,
                coupon.getValidTo()
        );

        return couponRepository.saveUserCoupon(userCoupon);
    }

    /**
     * 사용자 쿠폰 ID 생성 (임시)
     */
    private long generateUserCouponId() {
        return System.currentTimeMillis();
    }
}
