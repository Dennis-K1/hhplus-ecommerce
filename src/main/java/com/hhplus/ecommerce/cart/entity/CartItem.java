package com.hhplus.ecommerce.cart.entity;

import com.hhplus.ecommerce.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(
        name = "cart_item",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_product", columnNames = {"user_id", "product_id"}),
        indexes = @Index(name = "idx_user_id", columnList = "user_id")
)
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long cartItemId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    protected CartItem() {
    }

    public CartItem(Long userId, Long productId, Integer quantity) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getCartItemId() {
        return cartItemId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
