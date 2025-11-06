package com.hhplus.ecommerce.application.usecase;

import com.hhplus.ecommerce.domain.entity.Coupon;
import com.hhplus.ecommerce.domain.entity.UserCoupon;
import com.hhplus.ecommerce.domain.exception.CouponNotFoundException;
import com.hhplus.ecommerce.domain.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 쿠폰 유스케이스
 * - 쿠폰 조회/발급 비즈니스 로직
 */
@Service
@Transactional(readOnly = true)
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
     * - 비관적 락을 사용하여 동시성 제어
     */
    @Transactional
    public UserCoupon issueCoupon(Long userId, Long couponId) {
        // 쿠폰 조회 (비관적 락)
        Coupon coupon = couponRepository.findByIdWithLock(couponId)
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
