package com.hhplus.ecommerce.coupon.entity;

import com.hhplus.ecommerce.common.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "discount_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "issue_quantity", nullable = false)
    private Integer issueQuantity;

    @Column(name = "issued_quantity", nullable = false)
    private Integer issuedQuantity = 0;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDateTime validTo;

    protected Coupon() {
    }

    public Coupon(BigDecimal discountAmount, Integer issueQuantity, LocalDateTime validFrom, LocalDateTime validTo) {
        this.discountAmount = discountAmount;
        this.issueQuantity = issueQuantity;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public Long getCouponId() {
        return couponId;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public Integer getIssueQuantity() {
        return issueQuantity;
    }

    public Integer getIssuedQuantity() {
        return issuedQuantity;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public boolean isAvailableToIssue() {
        return issuedQuantity < issueQuantity;
    }

    public void increaseIssuedQuantity() {
        if (!isAvailableToIssue()) {
            throw new IllegalStateException("쿠폰 발급 수량이 초과되었습니다");
        }
        this.issuedQuantity++;
    }
}
