package com.hhplus.ecommerce.application.usecase;

import com.hhplus.ecommerce.domain.entity.Order;
import com.hhplus.ecommerce.domain.entity.OrderStatus;
import com.hhplus.ecommerce.domain.entity.Product;
import com.hhplus.ecommerce.domain.exception.InsufficientStockException;
import com.hhplus.ecommerce.domain.exception.OrderNotFoundException;
import com.hhplus.ecommerce.domain.exception.ProductNotFoundException;
import com.hhplus.ecommerce.mock.MockOrderRepository;
import com.hhplus.ecommerce.mock.MockProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderUseCase 단위 테스트
 * - Mock 구현체를 사용한 순수 단위 테스트
 * - Mockito 없이 실제 동작 검증
 */
class OrderUseCaseTest {

    private MockOrderRepository orderRepository;
    private MockProductRepository productRepository;
    private OrderUseCase orderUseCase;

    @BeforeEach
    void setUp() {
        orderRepository = new MockOrderRepository();
        productRepository = new MockProductRepository();
        orderUseCase = new OrderUseCase(orderRepository, productRepository);
    }

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_성공() {
        // Given
        Long userId = 1L;
        Product product = new Product(1L, "상품A", 10000, 100);
        productRepository.save(product);

        List<OrderUseCase.OrderItem> items = List.of(
                new OrderUseCase.OrderItem(1L, 2)
        );

        // When
        Order result = orderUseCase.createOrder(userId, items, null);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(20000, result.getTotalAmount());
        assertEquals(OrderStatus.PENDING, result.getOrderStatus());

        // 재고 차감 확인
        Product updatedProduct = productRepository.findById(1L).orElseThrow();
        assertEquals(98, updatedProduct.getStockQuantity());
    }

    @Test
    @DisplayName("주문 생성 성공 - 여러 상품")
    void createOrder_여러상품() {
        // Given
        Long userId = 1L;
        Product product1 = new Product(1L, "상품A", 10000, 100);
        Product product2 = new Product(2L, "상품B", 5000, 50);
        productRepository.save(product1);
        productRepository.save(product2);

        List<OrderUseCase.OrderItem> items = List.of(
                new OrderUseCase.OrderItem(1L, 1),
                new OrderUseCase.OrderItem(2L, 3)
        );

        // When
        Order result = orderUseCase.createOrder(userId, items, null);

        // Then
        assertNotNull(result);
        assertEquals(25000, result.getTotalAmount()); // 10000*1 + 5000*3

        // 재고 차감 확인
        assertEquals(99, productRepository.findById(1L).orElseThrow().getStockQuantity());
        assertEquals(47, productRepository.findById(2L).orElseThrow().getStockQuantity());
    }

    @Test
    @DisplayName("주문 생성 실패 - 상품 없음")
    void createOrder_상품없음() {
        // Given
        Long userId = 1L;
        List<OrderUseCase.OrderItem> items = List.of(
                new OrderUseCase.OrderItem(999L, 1)
        );

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> {
            orderUseCase.createOrder(userId, items, null);
        });
    }

    @Test
    @DisplayName("주문 생성 실패 - 재고 부족")
    void createOrder_재고부족() {
        // Given
        Long userId = 1L;
        Product lowStockProduct = new Product(1L, "상품A", 10000, 5);
        productRepository.save(lowStockProduct);

        List<OrderUseCase.OrderItem> items = List.of(
                new OrderUseCase.OrderItem(1L, 10) // 재고 5개인데 10개 주문
        );

        // When & Then
        assertThrows(InsufficientStockException.class, () -> {
            orderUseCase.createOrder(userId, items, null);
        });

        // 재고가 차감되지 않았는지 확인
        assertEquals(5, productRepository.findById(1L).orElseThrow().getStockQuantity());
    }

    @Test
    @DisplayName("주문 조회 성공")
    void getOrder_성공() {
        // Given
        Order order = new Order(1L, 1L, OrderStatus.PENDING, 20000, 0, 0);
        orderRepository.save(order);

        // When
        Order result = orderUseCase.getOrder(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(20000, result.getTotalAmount());
    }

    @Test
    @DisplayName("주문 조회 실패 - 주문 없음")
    void getOrder_주문없음() {
        // Given & When & Then
        assertThrows(OrderNotFoundException.class, () -> {
            orderUseCase.getOrder(999L);
        });
    }

    @Test
    @DisplayName("사용자 주문 목록 조회 성공")
    void getOrders_성공() {
        // Given
        Long userId = 1L;
        Order order1 = new Order(1L, userId, OrderStatus.PENDING, 20000, 0, 0);
        Order order2 = new Order(2L, userId, OrderStatus.COMPLETED, 30000, 0, 0);
        orderRepository.save(order1);
        orderRepository.save(order2);

        // When
        List<Order> result = orderUseCase.getOrders(userId, 1, 10);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("주문 개수 조회 성공")
    void getOrderCount_성공() {
        // Given
        Long userId = 1L;
        for (int i = 1; i <= 5; i++) {
            Order order = new Order((long) i, userId, OrderStatus.PENDING, 10000, 0, 0);
            orderRepository.save(order);
        }

        // When
        long result = orderUseCase.getOrderCount(userId);

        // Then
        assertEquals(5L, result);
    }

    @Test
    @DisplayName("주문 완료 처리 성공")
    void completeOrder_성공() {
        // Given
        Order order = new Order(1L, 1L, OrderStatus.PENDING, 20000, 0, 0);
        orderRepository.save(order);

        // When
        Order result = orderUseCase.completeOrder(1L);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.COMPLETED, result.getOrderStatus());

        // Repository에서도 완료 상태로 변경되었는지 확인
        Order savedOrder = orderRepository.findById(1L).orElseThrow();
        assertEquals(OrderStatus.COMPLETED, savedOrder.getOrderStatus());
    }

    @Test
    @DisplayName("주문 완료 처리 실패 - 주문 없음")
    void completeOrder_주문없음() {
        // Given & When & Then
        assertThrows(OrderNotFoundException.class, () -> {
            orderUseCase.completeOrder(999L);
        });
    }
}
