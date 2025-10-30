package com.hhplus.ecommerce.order.entity;

import com.hhplus.ecommerce.common.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_user_created", columnList = "user_id, created_at DESC"),
                @Index(name = "idx_created_status", columnList = "created_at DESC, order_status")
        }
)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 20)
    private OrderStatus orderStatus;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "discount_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "used_coupon_id")
    private Long usedCouponId;

    protected Order() {
    }

    public Order(Long userId, OrderStatus orderStatus, BigDecimal totalAmount) {
        this.userId = userId;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public Long getUsedCouponId() {
        return usedCouponId;
    }

    public void applyDiscount(BigDecimal discountAmount, Long usedCouponId) {
        this.discountAmount = discountAmount;
        this.usedCouponId = usedCouponId;
    }

    public void complete() {
        this.orderStatus = OrderStatus.COMPLETED;
    }

    public void cancel() {
        this.orderStatus = OrderStatus.CANCELLED;
    }

    public BigDecimal getFinalAmount() {
        return totalAmount.subtract(discountAmount);
    }
}
