package com.hhplus.ecommerce.domain.repository;

import com.hhplus.ecommerce.domain.entity.Coupon;
import com.hhplus.ecommerce.domain.entity.UserCoupon;

import java.util.List;
import java.util.Optional;

/**
 * 쿠폰 리포지토리 인터페이스
 * - 도메인 레이어에 위치하여 인프라스트럭처 독립성 유지
 */
public interface CouponRepository {

    /**
     * 쿠폰 조회
     */
    Optional<Coupon> findById(Long couponId);

    /**
     * 사용자 쿠폰 목록 조회
     */
    List<UserCoupon> findByUserId(Long userId);

    /**
     * 사용자 쿠폰 조회
     */
    Optional<UserCoupon> findUserCouponById(Long userCouponId);

    /**
     * 사용자 쿠폰 발급
     */
    UserCoupon saveUserCoupon(UserCoupon userCoupon);

    /**
     * 쿠폰 저장 (발급 수량 업데이트)
     */
    Coupon saveCoupon(Coupon coupon);

    /**
     * 비관적 락으로 쿠폰 조회 (동시성 제어)
     */
    Optional<Coupon> findByIdWithLock(Long couponId);
}
