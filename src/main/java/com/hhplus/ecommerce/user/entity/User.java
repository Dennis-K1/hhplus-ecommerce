package com.hhplus.ecommerce.user.entity;

import com.hhplus.ecommerce.common.BaseEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "point", nullable = false, precision = 15, scale = 2)
    private BigDecimal point = BigDecimal.ZERO;

    protected User() {
    }

    public User(BigDecimal point) {
        this.point = point;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getPoint() {
        return point;
    }

    public void chargePoint(BigDecimal amount) {
        this.point = this.point.add(amount);
    }

    public void deductPoint(BigDecimal amount) {
        if (this.point.compareTo(amount) < 0) {
            throw new IllegalStateException("포인트가 부족합니다");
        }
        this.point = this.point.subtract(amount);
    }
}
