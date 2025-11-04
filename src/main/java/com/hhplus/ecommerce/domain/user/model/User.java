package com.hhplus.ecommerce.domain.user.model;

import com.hhplus.ecommerce.common.exception.InvalidInputException;
import com.hhplus.ecommerce.domain.user.exception.InsufficientBalanceException;

/**
 * 사용자 도메인 모델
 * - 포인트 관리 핵심 비즈니스 로직
 * - 프레임워크와 독립적인 순수 POJO
 */
public class User {

    private final long id;
    private int point;

    public User(long id, int point) {
        validateUser(id, point);
        this.id = id;
        this.point = point;
    }

    private void validateUser(long id, int point) {
        if (id <= 0) {
            throw new InvalidInputException("사용자 ID는 0보다 커야 합니다");
        }
        if (point < 0) {
            throw new InvalidInputException("포인트는 0 이상이어야 합니다");
        }
    }

    /**
     * 포인트 충전
     */
    public void chargePoint(int amount) {
        if (amount <= 0) {
            throw new InvalidInputException("충전 금액은 0보다 커야 합니다");
        }
        this.point += amount;
    }

    /**
     * 포인트 차감
     */
    public void deductPoint(int amount) {
        if (amount <= 0) {
            throw new InvalidInputException("차감 금액은 0보다 커야 합니다");
        }
        if (!hasEnoughPoint(amount)) {
            throw new InsufficientBalanceException();
        }
        this.point -= amount;
    }

    /**
     * 포인트 잔액 확인
     */
    public boolean hasEnoughPoint(int amount) {
        return this.point >= amount;
    }

    public long getId() {
        return id;
    }

    public int getPoint() {
        return point;
    }
}
