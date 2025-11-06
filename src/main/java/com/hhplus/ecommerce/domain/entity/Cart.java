package com.hhplus.ecommerce.domain.entity;

import com.hhplus.ecommerce.common.exception.InvalidInputException;

/**
 * 장바구니 도메인 모델
 * - 프레임워크와 독립적인 순수 POJO
 */
public class Cart {

    private final long id;
    private final long userId;

    public Cart(long id, long userId) {
        validateCart(id, userId);
        this.id = id;
        this.userId = userId;
    }

    private void validateCart(long id, long userId) {
        if (id <= 0) {
            throw new InvalidInputException("장바구니 ID는 0보다 커야 합니다");
        }
        if (userId <= 0) {
            throw new InvalidInputException("사용자 ID는 0보다 커야 합니다");
        }
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }
}
