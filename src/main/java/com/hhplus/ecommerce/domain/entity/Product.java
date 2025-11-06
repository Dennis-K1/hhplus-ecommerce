package com.hhplus.ecommerce.domain.entity;

import com.hhplus.ecommerce.common.exception.InvalidInputException;
import com.hhplus.ecommerce.domain.exception.InsufficientStockException;
import com.hhplus.ecommerce.domain.exception.OutOfStockException;

/**
 * 상품 도메인 모델
 * - 재고 관리 핵심 비즈니스 로직
 * - 프레임워크와 독립적인 순수 POJO
 */
public class Product {

    private final long id;
    private final String name;
    private final int price;
    private int stockQuantity;

    public Product(long id, String name, int price, int stockQuantity) {
        validateProduct(id, name, price, stockQuantity);
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    private void validateProduct(long id, String name, int price, int stockQuantity) {
        if (id <= 0) {
            throw new InvalidInputException("상품 ID는 0보다 커야 합니다");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("상품명은 필수입니다");
        }
        if (price < 0) {
            throw new InvalidInputException("가격은 0 이상이어야 합니다");
        }
        if (stockQuantity < 0) {
            throw new InvalidInputException("재고는 0 이상이어야 합니다");
        }
    }

    /**
     * 재고 차감
     */
    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new InvalidInputException("차감할 수량은 0보다 커야 합니다");
        }
        if (isOutOfStock()) {
            throw new OutOfStockException();
        }
        if (!hasEnoughStock(quantity)) {
            throw new InsufficientStockException();
        }
        this.stockQuantity -= quantity;
    }

    /**
     * 재고 복구 (주문 취소 시)
     */
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new InvalidInputException("복구할 수량은 0보다 커야 합니다");
        }
        this.stockQuantity += quantity;
    }

    /**
     * 재고 확인
     */
    public boolean hasEnoughStock(int quantity) {
        return this.stockQuantity >= quantity;
    }

    /**
     * 재고 소진 여부
     */
    public boolean isOutOfStock() {
        return this.stockQuantity == 0;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }
}
