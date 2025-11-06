package com.hhplus.ecommerce.mock;

import com.hhplus.ecommerce.domain.entity.Cart;
import com.hhplus.ecommerce.domain.entity.CartItem;
import com.hhplus.ecommerce.domain.repository.CartRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * CartRepository Mock 구현
 * - 테스트용 인메모리 저장소
 * - 동시성 테스트를 위해 ConcurrentHashMap 사용
 */
public class MockCartRepository implements CartRepository {

    // 동시성 제어를 위한 ConcurrentHashMap
    private final Map<Long, Cart> carts = new ConcurrentHashMap<>();
    private final Map<Long, CartItem> cartItems = new ConcurrentHashMap<>();

    // 자동 증가 ID
    private final AtomicLong cartIdGenerator = new AtomicLong(1L);
    private final AtomicLong cartItemIdGenerator = new AtomicLong(1L);

    public MockCartRepository() {
        // 빈 상태로 시작 - 각 테스트에서 필요한 데이터를 직접 추가
    }

    @Override
    public Optional<Cart> findByUserId(Long userId) {
        return carts.values().stream()
                .filter(cart -> cart.getUserId() == userId)
                .findFirst();
    }

    @Override
    public List<CartItem> findCartItemsByUserId(Long userId) {
        // userId를 cartId로 간주 (간소화)
        return cartItems.values().stream()
                .filter(item -> item.getCartId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CartItem> findCartItemById(Long cartItemId) {
        return Optional.ofNullable(cartItems.get(cartItemId));
    }

    @Override
    public CartItem saveCartItem(CartItem cartItem) {
        // ID가 없으면 자동 생성
        if (cartItem.getId() == 0) {
            long newId = cartItemIdGenerator.getAndIncrement();
            CartItem newItem = new CartItem(
                    newId,
                    cartItem.getCartId(),
                    cartItem.getProductId(),
                    cartItem.getQuantity()
            );
            cartItems.put(newId, newItem);
            return newItem;
        }

        cartItems.put(cartItem.getId(), cartItem);
        return cartItem;
    }

    @Override
    public void deleteCartItem(Long cartItemId) {
        cartItems.remove(cartItemId);
    }
}
