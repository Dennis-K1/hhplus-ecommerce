package com.hhplus.ecommerce.domain.order.model;

import com.hhplus.ecommerce.common.exception.InvalidInputException;
import com.hhplus.ecommerce.domain.order.exception.InvalidOrderStatusException;

/**
 * 주문 도메인 모델
 * - 주문 생성 및 상태 관리 핵심 비즈니스 로직
 * - 프레임워크와 독립적인 순수 POJO
 */
public class Order {

    private final long id;
    private final long userId;
    private OrderStatus orderStatus;
    private final int totalAmount;
    private int discountAmount;
    private long usedCouponId;

    public Order(long id, long userId, OrderStatus orderStatus, int totalAmount,
                 int discountAmount, long usedCouponId) {
        validateOrder(id, userId, orderStatus, totalAmount, discountAmount);
        this.id = id;
        this.userId = userId;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.usedCouponId = usedCouponId;
    }

    private void validateOrder(long id, long userId, OrderStatus orderStatus, int totalAmount, int discountAmount) {
        if (id <= 0) {
            throw new InvalidInputException("주문 ID는 0보다 커야 합니다");
        }
        if (userId <= 0) {
            throw new InvalidInputException("사용자 ID는 0보다 커야 합니다");
        }
        if (orderStatus == null) {
            throw new InvalidInputException("주문 상태는 필수입니다");
        }
        if (totalAmount < 0) {
            throw new InvalidInputException("총 금액은 0 이상이어야 합니다");
        }
        if (discountAmount < 0) {
            throw new InvalidInputException("할인 금액은 0 이상이어야 합니다");
        }
        if (discountAmount > totalAmount) {
            throw new InvalidInputException("할인 금액은 총 금액을 초과할 수 없습니다");
        }
    }

    /**
     * 할인 적용
     */
    public void applyDiscount(int discountAmount, long usedCouponId) {
        if (discountAmount < 0) {
            throw new InvalidInputException("할인 금액은 0 이상이어야 합니다");
        }
        if (discountAmount > totalAmount) {
            throw new InvalidInputException("할인 금액은 총 금액을 초과할 수 없습니다");
        }
        this.discountAmount = discountAmount;
        this.usedCouponId = usedCouponId;
    }

    /**
     * 주문 완료 처리
     */
    public void complete() {
        if (orderStatus == OrderStatus.COMPLETED) {
            throw new InvalidOrderStatusException();
        }
        if (orderStatus == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException();
        }
        this.orderStatus = OrderStatus.COMPLETED;
    }

    /**
     * 주문 취소 처리
     */
    public void cancel() {
        if (orderStatus == OrderStatus.COMPLETED) {
            throw new InvalidOrderStatusException();
        }
        if (orderStatus == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException();
        }
        this.orderStatus = OrderStatus.CANCELLED;
    }

    /**
     * 최종 결제 금액 계산
     */
    public int getFinalAmount() {
        return totalAmount - discountAmount;
    }

    /**
     * 주문이 완료 상태인지 확인
     */
    public boolean isCompleted() {
        return orderStatus == OrderStatus.COMPLETED;
    }

    /**
     * 주문이 취소 상태인지 확인
     */
    public boolean isCancelled() {
        return orderStatus == OrderStatus.CANCELLED;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public long getUsedCouponId() {
        return usedCouponId;
    }
}
