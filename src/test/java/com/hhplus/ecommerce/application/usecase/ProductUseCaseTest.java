package com.hhplus.ecommerce.application.usecase;

import com.hhplus.ecommerce.domain.entity.Product;
import com.hhplus.ecommerce.domain.exception.ProductNotFoundException;
import com.hhplus.ecommerce.mock.MockProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ProductUseCase 단위 테스트
 * - Mock 구현체를 사용한 순수 단위 테스트
 * - Mockito 없이 실제 동작 검증
 */
class ProductUseCaseTest {

    private MockProductRepository productRepository;
    private ProductUseCase productUseCase;

    @BeforeEach
    void setUp() {
        productRepository = new MockProductRepository();
        productUseCase = new ProductUseCase(productRepository);
    }

    @Test
    @DisplayName("상품 목록 조회 성공")
    void getProducts_성공() {
        // Given
        int page = 1;
        int size = 10;
        String search = null;

        Product product1 = new Product(1L, "상품A", 10000, 100);
        Product product2 = new Product(2L, "상품B", 5000, 50);
        productRepository.save(product1);
        productRepository.save(product2);

        // When
        List<Product> result = productUseCase.getProducts(page, size, search);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("상품A", result.get(0).getName());
        assertEquals("상품B", result.get(1).getName());
    }

    @Test
    @DisplayName("상품 목록 조회 성공 - 검색어 있음")
    void getProducts_검색어() {
        // Given
        int page = 1;
        int size = 10;
        String search = "상품A";

        Product product1 = new Product(1L, "상품A", 10000, 100);
        Product product2 = new Product(2L, "상품B", 5000, 50);
        productRepository.save(product1);
        productRepository.save(product2);

        // When
        List<Product> result = productUseCase.getProducts(page, size, search);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("상품A", result.get(0).getName());
    }

    @Test
    @DisplayName("상품 개수 조회 성공")
    void getProductCount_성공() {
        // Given
        String search = null;
        for (int i = 1; i <= 10; i++) {
            Product product = new Product((long) i, "상품" + i, 10000, 100);
            productRepository.save(product);
        }

        // When
        long result = productUseCase.getProductCount(search);

        // Then
        assertEquals(10L, result);
    }

    @Test
    @DisplayName("상품 개수 조회 성공 - 검색어 있음")
    void getProductCount_검색어() {
        // Given
        String search = "상품A";
        Product product1 = new Product(1L, "상품A", 10000, 100);
        Product product2 = new Product(2L, "상품B", 5000, 50);
        productRepository.save(product1);
        productRepository.save(product2);

        // When
        long result = productUseCase.getProductCount(search);

        // Then
        assertEquals(1L, result);
    }

    @Test
    @DisplayName("상품 상세 조회 성공")
    void getProduct_성공() {
        // Given
        Long productId = 1L;
        Product product = new Product(productId, "상품A", 10000, 100);
        productRepository.save(product);

        // When
        Product result = productUseCase.getProduct(productId);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("상품A", result.getName());
        assertEquals(10000, result.getPrice());
    }

    @Test
    @DisplayName("상품 상세 조회 실패 - 상품 없음")
    void getProduct_상품없음() {
        // Given
        Long productId = 999L;

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> {
            productUseCase.getProduct(productId);
        });
    }

    @Test
    @DisplayName("인기 상품 조회 성공")
    void getTopProducts_성공() {
        // Given
        Product product1 = new Product(1L, "상품A", 10000, 100);
        Product product2 = new Product(2L, "상품B", 5000, 50);
        Product product3 = new Product(3L, "상품C", 7000, 30);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        // When
        List<Product> result = productUseCase.getTopProducts();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("재고 확인 성공 - 재고 충분")
    void checkStock_재고충분() {
        // Given
        Long productId = 1L;
        Product product = new Product(productId, "상품A", 10000, 100);
        productRepository.save(product);
        int quantity = 50; // 재고 100개 중 50개 확인

        // When
        boolean result = productUseCase.checkStock(productId, quantity);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("재고 확인 성공 - 재고 부족")
    void checkStock_재고부족() {
        // Given
        Long productId = 1L;
        Product product = new Product(productId, "상품A", 10000, 100);
        productRepository.save(product);
        int quantity = 150; // 재고 100개인데 150개 확인

        // When
        boolean result = productUseCase.checkStock(productId, quantity);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("재고 확인 실패 - 상품 없음")
    void checkStock_상품없음() {
        // Given
        Long productId = 999L;
        int quantity = 10;

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> {
            productUseCase.checkStock(productId, quantity);
        });
    }
}
