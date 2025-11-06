package com.hhplus.ecommerce.domain.entity;

import com.hhplus.ecommerce.common.exception.InvalidInputException;

/**
 * 장바구니 아이템 도메인 모델
 * - 수량 변경 핵심 비즈니스 로직
 * - 프레임워크와 독립적인 순수 POJO
 */
public class CartItem {

    private final long id;
    private final long cartId;
    private final long productId;
    private int quantity;

    public CartItem(long id, long cartId, long productId, int quantity) {
        validateCartItem(id, cartId, productId, quantity);
        this.id = id;
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
    }

    private void validateCartItem(long id, long cartId, long productId, int quantity) {
        if (id <= 0) {
            throw new InvalidInputException("장바구니 아이템 ID는 0보다 커야 합니다");
        }
        if (cartId <= 0) {
            throw new InvalidInputException("장바구니 ID는 0보다 커야 합니다");
        }
        if (productId <= 0) {
            throw new InvalidInputException("상품 ID는 0보다 커야 합니다");
        }
        if (quantity <= 0) {
            throw new InvalidInputException("수량은 0보다 커야 합니다");
        }
    }

    /**
     * 수량 변경
     */
    public void updateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InvalidInputException("수량은 0보다 커야 합니다");
        }
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public long getCartId() {
        return cartId;
    }

    public long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
