package com.hhplus.ecommerce.application.usecase;

import com.hhplus.ecommerce.domain.entity.CartItem;
import com.hhplus.ecommerce.domain.entity.Product;
import com.hhplus.ecommerce.domain.exception.CartItemNotFoundException;
import com.hhplus.ecommerce.domain.exception.InsufficientStockException;
import com.hhplus.ecommerce.domain.exception.ProductNotFoundException;
import com.hhplus.ecommerce.domain.repository.CartRepository;
import com.hhplus.ecommerce.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 장바구니 유스케이스
 * - 장바구니 조회/추가/수정/삭제 비즈니스 로직
 */
@Service
@Transactional(readOnly = true)
public class CartUseCase {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartUseCase(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    /**
     * 장바구니 조회
     */
    public List<CartItem> getCart(Long userId) {
        return cartRepository.findCartItemsByUserId(userId);
    }

    /**
     * 장바구니에 상품 추가
     */
    @Transactional
    public CartItem addToCart(Long userId, Long productId, int quantity) {
        // 상품 존재 여부 확인
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        // 재고 확인
        if (!product.hasEnoughStock(quantity)) {
            throw new InsufficientStockException();
        }

        // 장바구니 아이템 생성
        CartItem cartItem = new CartItem(
                generateCartItemId(),
                userId,  // cartId 대신 userId 사용 (간소화)
                productId,
                quantity
        );

        return cartRepository.saveCartItem(cartItem);
    }

    /**
     * 장바구니 수량 수정
     */
    @Transactional
    public CartItem updateQuantity(Long userId, Long cartItemId, int quantity) {
        CartItem cartItem = cartRepository.findCartItemById(cartItemId)
                .orElseThrow(CartItemNotFoundException::new);

        // 재고 확인
        Product product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(ProductNotFoundException::new);

        if (!product.hasEnoughStock(quantity)) {
            throw new InsufficientStockException();
        }

        cartItem.updateQuantity(quantity);
        return cartRepository.saveCartItem(cartItem);
    }

    /**
     * 장바구니에서 상품 삭제
     */
    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        cartRepository.deleteCartItem(cartItemId);
    }

    /**
     * 장바구니 아이템 ID 생성 (임시)
     */
    private long generateCartItemId() {
        return System.currentTimeMillis();
    }
}
