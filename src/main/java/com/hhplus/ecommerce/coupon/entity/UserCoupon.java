package com.hhplus.ecommerce.coupon.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_coupon",
        indexes = @Index(name = "idx_user_used", columnList = "user_id, is_used")
)
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_id")
    private Long userCouponId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.issuedAt == null) {
            this.issuedAt = LocalDateTime.now();
        }
    }

    protected UserCoupon() {
    }

    public UserCoupon(Long userId, Long couponId, LocalDateTime expiredAt) {
        this.userId = userId;
        this.couponId = couponId;
        this.issuedAt = LocalDateTime.now();
        this.expiredAt = expiredAt;
    }

    public Long getUserCouponId() {
        return userCouponId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Boolean getIsUsed() {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    public void use(Long orderId) {
        if (isUsed) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다");
        }
        if (isExpired()) {
            throw new IllegalStateException("만료된 쿠폰입니다");
        }
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
        this.orderId = orderId;
    }
}
