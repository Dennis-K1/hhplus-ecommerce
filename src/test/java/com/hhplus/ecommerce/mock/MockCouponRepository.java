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
 * - 동시성 테스트를 위해 ConcurrentHashMap 사용
 */
public class MockCouponRepository implements CouponRepository {

    // 동시성 제어를 위한 ConcurrentHashMap
    private final Map<Long, Coupon> coupons = new ConcurrentHashMap<>();
    private final Map<Long, UserCoupon> userCoupons = new ConcurrentHashMap<>();

    // 비관적 락 시뮬레이션을 위한 락 맵
    private final Map<Long, Object> locks = new ConcurrentHashMap<>();

    // 자동 증가 ID
    private final AtomicLong userCouponIdGenerator = new AtomicLong(1L);

    public MockCouponRepository() {
        // 빈 상태로 시작 - 각 테스트에서 필요한 데이터를 saveCoupon()으로 추가
    }

    @Override
    public Optional<Coupon> findById(Long couponId) {
        return Optional.ofNullable(coupons.get(couponId));
    }

    @Override
    public List<UserCoupon> findByUserId(Long userId) {
        return userCoupons.values().stream()
                .filter(uc -> uc.getUserId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserCoupon> findUserCouponById(Long userCouponId) {
        return Optional.ofNullable(userCoupons.get(userCouponId));
    }

    @Override
    public UserCoupon saveUserCoupon(UserCoupon userCoupon) {
        // ID가 없으면 자동 생성
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
    public Coupon saveCoupon(Coupon coupon) {
        coupons.put(coupon.getId(), coupon);
        locks.putIfAbsent(coupon.getId(), new Object());  // lock도 함께 추가
        return coupon;
    }

    /**
     * 비관적 락으로 쿠폰 조회 (동시성 제어)
     * - synchronized 블록으로 원자적 처리 보장
     */
    @Override
    public Optional<Coupon> findByIdWithLock(Long couponId) {
        // 락 객체가 없으면 생성
        locks.putIfAbsent(couponId, new Object());

        // 해당 쿠폰의 락을 획득하고 조회
        synchronized (locks.get(couponId)) {
            return Optional.ofNullable(coupons.get(couponId));
        }
    }
}
