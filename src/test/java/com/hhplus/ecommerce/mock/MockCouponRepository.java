package com.hhplus.ecommerce.mock;

import com.hhplus.ecommerce.domain.entity.Coupon;
import com.hhplus.ecommerce.domain.entity.UserCoupon;
import com.hhplus.ecommerce.domain.repository.CouponRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * CouponRepository Mock 구현
 * - 테스트용 인메모리 저장소
 * - synchronized로 DB 비관적 락 시뮬레이션
 * - 실제 프로덕션에서는 JPA @Lock(PESSIMISTIC_WRITE) 사용 필요
 */
public class MockCouponRepository implements CouponRepository {

    private final Map<Long, Coupon> coupons = new ConcurrentHashMap<>();
    private final Map<Long, UserCoupon> userCoupons = new ConcurrentHashMap<>();
    private final AtomicLong userCouponIdGenerator = new AtomicLong(1L);

    @Override
    public synchronized Optional<Coupon> findById(Long couponId) {
        return Optional.ofNullable(coupons.get(couponId));
    }

    @Override
    public List<UserCoupon> findByUserId(Long userId) {
        return userCoupons.values().stream()
                .filter(uc -> uc.getUserId() == userId)
                .collect(Collectors.toList());
    }

    /**
     * 특정 쿠폰 ID로 발급된 모든 UserCoupon 조회
     */
    public List<UserCoupon> findByCouponId(Long couponId) {
        return userCoupons.values().stream()
                .filter(uc -> uc.getCouponId() == couponId)
                .collect(Collectors.toList());
    }

    /**
     * 비관적 락을 시뮬레이션하는 조회 메서드
     * synchronized로 동시성 제어
     */
    public synchronized Optional<Coupon> findByIdForUpdate(Long couponId) {
        return Optional.ofNullable(coupons.get(couponId));
    }

    @Override
    public Optional<UserCoupon> findUserCouponById(Long userCouponId) {
        return Optional.ofNullable(userCoupons.get(userCouponId));
    }

    @Override
    public UserCoupon saveUserCoupon(UserCoupon userCoupon) {
        if (userCoupon.getId() == 0) {
            long newId = userCouponIdGenerator.getAndIncrement();
            UserCoupon newUserCoupon = new UserCoupon(
                    newId,
                    userCoupon.getUserId(),
                    userCoupon.getCouponId(),
                    userCoupon.getOrderId(),
                    userCoupon.isUsed(),
                    userCoupon.getIssuedAt(),
                    userCoupon.getUsedAt(),
                    userCoupon.getExpiredAt()
            );
            userCoupons.put(newId, newUserCoupon);
            return newUserCoupon;
        }

        userCoupons.put(userCoupon.getId(), userCoupon);
        return userCoupon;
    }

    @Override
    public synchronized Coupon saveCoupon(Coupon coupon) {
        coupons.put(coupon.getId(), coupon);
        return coupon;
    }
}
