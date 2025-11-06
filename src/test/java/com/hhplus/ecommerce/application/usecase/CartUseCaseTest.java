package com.hhplus.ecommerce.application.usecase;

import com.hhplus.ecommerce.domain.entity.CartItem;
import com.hhplus.ecommerce.domain.entity.Product;
import com.hhplus.ecommerce.domain.exception.CartItemNotFoundException;
import com.hhplus.ecommerce.domain.exception.InsufficientStockException;
import com.hhplus.ecommerce.domain.exception.ProductNotFoundException;
import com.hhplus.ecommerce.mock.MockCartRepository;
import com.hhplus.ecommerce.mock.MockProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CartUseCase 단위 테스트
 * - Mock 구현체를 사용한 순수 단위 테스트
 * - Mockito 없이 실제 동작 검증
 */
class CartUseCaseTest {

    private MockCartRepository cartRepository;
    private MockProductRepository productRepository;
    private CartUseCase cartUseCase;

    @BeforeEach
    void setUp() {
        cartRepository = new MockCartRepository();
        productRepository = new MockProductRepository();
        cartUseCase = new CartUseCase(cartRepository, productRepository);
    }

    @Test
    @DisplayName("장바구니 조회 성공")
    void getCart_성공() {
        // Given
        Long userId = 1L;
        CartItem cartItem = new CartItem(1L, userId, 1L, 2);
        cartRepository.saveCartItem(cartItem);

        // When
        List<CartItem> result = cartUseCase.getCart(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getCartId()); // cartId를 userId로 사용
    }

    @Test
    @DisplayName("장바구니에 상품 추가 성공")
    void addToCart_성공() {
        // Given
        Long userId = 1L;
        Long productId = 1L;
        int quantity = 5;
        Product product = new Product(productId, "상품A", 10000, 100);
        productRepository.save(product);

        // When
        CartItem result = cartUseCase.addToCart(userId, productId, quantity);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getCartId()); // cartId를 userId로 사용
        assertEquals(productId, result.getProductId());
        assertEquals(quantity, result.getQuantity());
    }

    @Test
    @DisplayName("장바구니에 상품 추가 실패 - 상품 없음")
    void addToCart_상품없음() {
        // Given
        Long userId = 1L;
        Long productId = 999L;
        int quantity = 5;

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> {
            cartUseCase.addToCart(userId, productId, quantity);
        });
    }

    @Test
    @DisplayName("장바구니에 상품 추가 실패 - 재고 부족")
    void addToCart_재고부족() {
        // Given
        Long userId = 1L;
        Long productId = 1L;
        int quantity = 200; // 재고 100개인데 200개 요청
        Product product = new Product(productId, "상품A", 10000, 100);
        productRepository.save(product);

        // When & Then
        assertThrows(InsufficientStockException.class, () -> {
            cartUseCase.addToCart(userId, productId, quantity);
        });
    }

    @Test
    @DisplayName("장바구니 수량 수정 성공")
    void updateQuantity_성공() {
        // Given
        Long userId = 1L;
        Long productId = 1L;
        Product product = new Product(productId, "상품A", 10000, 100);
        productRepository.save(product);

        CartItem cartItem = new CartItem(1L, userId, productId, 2);
        CartItem savedItem = cartRepository.saveCartItem(cartItem);
        int newQuantity = 10;

        // When
        CartItem result = cartUseCase.updateQuantity(userId, savedItem.getId(), newQuantity);

        // Then
        assertNotNull(result);
        assertEquals(newQuantity, result.getQuantity());

        // Repository에서도 변경 확인
        CartItem updatedItem = cartRepository.findCartItemById(savedItem.getId()).orElseThrow();
        assertEquals(newQuantity, updatedItem.getQuantity());
    }

    @Test
    @DisplayName("장바구니 수량 수정 실패 - 장바구니 아이템 없음")
    void updateQuantity_아이템없음() {
        // Given
        Long userId = 1L;
        Long cartItemId = 999L;
        int newQuantity = 10;

        // When & Then
        assertThrows(CartItemNotFoundException.class, () -> {
            cartUseCase.updateQuantity(userId, cartItemId, newQuantity);
        });
    }

    @Test
    @DisplayName("장바구니 수량 수정 실패 - 재고 부족")
    void updateQuantity_재고부족() {
        // Given
        Long userId = 1L;
        Long productId = 1L;
        Product product = new Product(productId, "상품A", 10000, 100);
        productRepository.save(product);

        CartItem cartItem = new CartItem(1L, userId, productId, 2);
        CartItem savedItem = cartRepository.saveCartItem(cartItem);
        int newQuantity = 200; // 재고 100개인데 200개로 수정

        // When & Then
        assertThrows(InsufficientStockException.class, () -> {
            cartUseCase.updateQuantity(userId, savedItem.getId(), newQuantity);
        });

        // 수량이 변경되지 않았는지 확인
        CartItem unchangedItem = cartRepository.findCartItemById(savedItem.getId()).orElseThrow();
        assertEquals(2, unchangedItem.getQuantity());
    }

    @Test
    @DisplayName("장바구니에서 상품 삭제 성공")
    void removeFromCart_성공() {
        // Given
        Long userId = 1L;
        CartItem cartItem = new CartItem(1L, userId, 1L, 2);
        CartItem savedItem = cartRepository.saveCartItem(cartItem);

        // When
        cartUseCase.removeFromCart(userId, savedItem.getId());

        // Then
        assertTrue(cartRepository.findCartItemById(savedItem.getId()).isEmpty());
    }
}
