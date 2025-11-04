package com.hhplus.ecommerce.domain.coupon.model;

import com.hhplus.ecommerce.common.exception.InvalidInputException;
import com.hhplus.ecommerce.domain.coupon.exception.CouponAlreadyUsedException;
import com.hhplus.ecommerce.domain.coupon.exception.CouponExpiredException;

import java.time.LocalDateTime;

/**
 * 사용자 쿠폰 도메인 모델
 * - 쿠폰 사용 핵심 비즈니스 로직
 * - 프레임워크와 독립적인 순수 POJO
 */
public class UserCoupon {

    private final long id;
    private final long userId;
    private final long couponId;
    private long orderId;
    private boolean isUsed;
    private final LocalDateTime issuedAt;
    private LocalDateTime usedAt;
    private final LocalDateTime expiredAt;

    public UserCoupon(long id, long userId, long couponId, long orderId, boolean isUsed,
                      LocalDateTime issuedAt, LocalDateTime usedAt, LocalDateTime expiredAt) {
        validateUserCoupon(id, userId, couponId, issuedAt, expiredAt);
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.orderId = orderId;
        this.isUsed = isUsed;
        this.issuedAt = issuedAt;
        this.usedAt = usedAt;
        this.expiredAt = expiredAt;
    }

    private void validateUserCoupon(long id, long userId, long couponId, LocalDateTime issuedAt, LocalDateTime expiredAt) {
        if (id <= 0) {
            throw new InvalidInputException("사용자 쿠폰 ID는 0보다 커야 합니다");
        }
        if (userId <= 0) {
            throw new InvalidInputException("사용자 ID는 0보다 커야 합니다");
        }
        if (couponId <= 0) {
            throw new InvalidInputException("쿠폰 ID는 0보다 커야 합니다");
        }
        if (issuedAt == null) {
            throw new InvalidInputException("발급일시는 필수입니다");
        }
        if (expiredAt == null) {
            throw new InvalidInputException("만료일시는 필수입니다");
        }
        if (issuedAt.isAfter(expiredAt)) {
            throw new InvalidInputException("발급일시가 만료일시보다 이후일 수 없습니다");
        }
    }

    /**
     * 쿠폰 사용 가능 여부 확인
     */
    public boolean canUse(LocalDateTime now) {
        return !isUsed && !isExpired(now);
    }

    /**
     * 쿠폰 만료 여부 확인
     */
    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(expiredAt);
    }

    /**
     * 쿠폰 사용 처리
     */
    public void use(long orderId, LocalDateTime now) {
        if (orderId <= 0) {
            throw new InvalidInputException("주문 ID는 필수입니다");
        }
        if (isUsed) {
            throw new CouponAlreadyUsedException();
        }
        if (isExpired(now)) {
            throw new CouponExpiredException();
        }
        this.isUsed = true;
        this.usedAt = now;
        this.orderId = orderId;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getCouponId() {
        return couponId;
    }

    public long getOrderId() {
        return orderId;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }
}
