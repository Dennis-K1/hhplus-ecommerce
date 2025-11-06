package com.hhplus.ecommerce.domain.entity;

import com.hhplus.ecommerce.common.exception.InvalidInputException;
import com.hhplus.ecommerce.domain.exception.CouponNotIssuablePeriodException;
import com.hhplus.ecommerce.domain.exception.CouponSoldOutException;

import java.time.LocalDateTime;

/**
 * 쿠폰 도메인 모델
 * - 쿠폰 발급 핵심 비즈니스 로직
 * - 프레임워크와 독립적인 순수 POJO
 */
public class Coupon {

    private final long id;
    private final int discountAmount;
    private final int issueQuantity;
    private int issuedQuantity;
    private final LocalDateTime validFrom;
    private final LocalDateTime validTo;

    public Coupon(long id, int discountAmount, int issueQuantity, int issuedQuantity,
                  LocalDateTime validFrom, LocalDateTime validTo) {
        validateCoupon(id, discountAmount, issueQuantity, issuedQuantity, validFrom, validTo);
        this.id = id;
        this.discountAmount = discountAmount;
        this.issueQuantity = issueQuantity;
        this.issuedQuantity = issuedQuantity;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    private void validateCoupon(long id, int discountAmount, int issueQuantity, int issuedQuantity,
                                LocalDateTime validFrom, LocalDateTime validTo) {
        if (id <= 0) {
            throw new InvalidInputException("쿠폰 ID는 0보다 커야 합니다");
        }
        if (discountAmount <= 0) {
            throw new InvalidInputException("할인 금액은 0보다 커야 합니다");
        }
        if (issueQuantity <= 0) {
            throw new InvalidInputException("발급 수량은 0보다 커야 합니다");
        }
        if (issuedQuantity < 0 || issuedQuantity > issueQuantity) {
            throw new InvalidInputException("발급된 수량이 유효하지 않습니다");
        }
        if (validFrom == null || validTo == null) {
            throw new InvalidInputException("유효 기간은 필수입니다");
        }
        if (validFrom.isAfter(validTo)) {
            throw new InvalidInputException("유효 기간이 올바르지 않습니다");
        }
    }

    /**
     * 발급 가능 여부 확인 (수량)
     */
    public boolean hasIssueQuantity() {
        return issuedQuantity < issueQuantity;
    }

    /**
     * 발급 가능 여부 확인 (수량 + 기간)
     */
    public boolean canIssue(LocalDateTime now) {
        return !isExpired(now) && hasIssueQuantity();
    }

    /**
     * 쿠폰 발급 처리
     */
    public void issue(LocalDateTime now) {
        if (isExpired(now)) {
            throw new CouponNotIssuablePeriodException();
        }
        if (!hasIssueQuantity()) {
            throw new CouponSoldOutException();
        }
        this.issuedQuantity++;
    }

    /**
     * 쿠폰 발급 기간 만료 여부 확인
     */
    public boolean isExpired(LocalDateTime now) {
        return now.isBefore(validFrom) || now.isAfter(validTo);
    }

    public long getId() {
        return id;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public int getIssueQuantity() {
        return issueQuantity;
    }

    public int getIssuedQuantity() {
        return issuedQuantity;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }
}
