package com.hhplus.ecommerce.domain.repository;

import com.hhplus.ecommerce.domain.entity.Cart;
import com.hhplus.ecommerce.domain.entity.CartItem;

import java.util.List;
import java.util.Optional;

/**
 * 장바구니 리포지토리 인터페이스
 * - 도메인 레이어에 위치하여 인프라스트럭처 독립성 유지
 */
public interface CartRepository {

    /**
     * 사용자의 장바구니 조회
     */
    Optional<Cart> findByUserId(Long userId);

    /**
     * 장바구니 아이템 목록 조회
     */
    List<CartItem> findCartItemsByUserId(Long userId);

    /**
     * 장바구니 아이템 조회
     */
    Optional<CartItem> findCartItemById(Long cartItemId);

    /**
     * 장바구니 아이템 저장
     */
    CartItem saveCartItem(CartItem cartItem);

    /**
     * 장바구니 아이템 삭제
     */
    void deleteCartItem(Long cartItemId);
}
