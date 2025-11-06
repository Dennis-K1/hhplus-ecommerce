package com.hhplus.ecommerce.application.usecase;

import com.hhplus.ecommerce.domain.entity.Product;
import com.hhplus.ecommerce.domain.exception.InsufficientStockException;
import com.hhplus.ecommerce.mock.MockOrderRepository;
import com.hhplus.ecommerce.mock.MockProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 주문 유스케이스 동시성 테스트
 * - 여러 스레드가 동시에 같은 상품을 주문할 때 재고 정합성 검증
 */
class OrderUseCaseConcurrencyTest {

    private MockProductRepository productRepository;
    private MockOrderRepository orderRepository;
    private OrderUseCase orderUseCase;

    @BeforeEach
    void setUp() {
        productRepository = new MockProductRepository();
        orderRepository = new MockOrderRepository();
        orderUseCase = new OrderUseCase(orderRepository, productRepository);
    }

    @Test
    @DisplayName("동시에 100명이 재고 10개인 상품 주문 - 10명만 성공해야 함")
    void 동시_주문_재고_차감_테스트() throws InterruptedException {
        // Given: 재고 10개인 상품
        Product product = new Product(1L, "한정판 상품", 100000, 10);
        productRepository.save(product);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When: 100명이 동시에 1개씩 주문 시도
        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1L;
            executorService.submit(() -> {
                try {
                    List<OrderUseCase.OrderItem> items = List.of(
                            new OrderUseCase.OrderItem(1L, 1)
                    );
                    orderUseCase.createOrder(userId, items, null);
                    successCount.incrementAndGet();
                } catch (InsufficientStockException | com.hhplus.ecommerce.domain.exception.OutOfStockException e) {
                    // 재고 부족 예외는 정상
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then: 정확히 10명만 성공, 90명은 실패
        assertEquals(10, successCount.get(), "재고 10개이므로 10명만 성공해야 함");
        assertEquals(90, failCount.get(), "나머지 90명은 재고 부족으로 실패해야 함");

        // 최종 재고 확인
        Product finalProduct = productRepository.findById(1L).orElseThrow();
        assertEquals(0, finalProduct.getStockQuantity(), "최종 재고는 0이어야 함");
    }

    @Test
    @DisplayName("동시에 50명이 2개씩 주문 - 재고 100개면 모두 성공")
    void 동시_주문_충분한_재고() throws InterruptedException {
        // Given: 재고 100개인 상품
        Product product = new Product(1L, "일반 상품", 50000, 100);
        productRepository.save(product);

        int threadCount = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);

        // When: 50명이 동시에 2개씩 주문
        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1L;
            executorService.submit(() -> {
                try {
                    List<OrderUseCase.OrderItem> items = List.of(
                            new OrderUseCase.OrderItem(1L, 2)
                    );
                    orderUseCase.createOrder(userId, items, null);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    fail("충분한 재고가 있으므로 실패하면 안됨: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then: 모두 성공
        assertEquals(50, successCount.get(), "모든 주문이 성공해야 함");

        // 최종 재고 확인 (100 - 50*2 = 0)
        Product finalProduct = productRepository.findById(1L).orElseThrow();
        assertEquals(0, finalProduct.getStockQuantity(), "최종 재고는 0이어야 함");
    }

    @Test
    @DisplayName("비관적 락 없이 동시 주문 시 재고 정합성 깨짐 확인")
    void 비관적락_없이_동시_주문() throws InterruptedException {
        // Given: 재고 50개인 상품
        Product product = new Product(1L, "테스트 상품", 10000, 50);
        productRepository.save(product);

        int threadCount = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // When: 비관적 락 없이 동시 접근 (findById 사용)
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // ❌ findByIdWithLock 대신 findById 사용 (락 없음)
                    Product p = productRepository.findById(1L).orElseThrow();

                    // 재고 차감 (동시성 문제 발생 가능)
                    p.decreaseStock(1);
                    productRepository.save(p);
                } catch (Exception e) {
                    // 예외 무시
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then: 락이 없으면 재고가 정확하지 않을 수 있음
        Product finalProduct = productRepository.findById(1L).orElseThrow();

        // 이 테스트는 동시성 문제를 보여주기 위한 것
        // 실제로는 비관적 락(findByIdWithLock)을 사용해야 함
        System.out.println("비관적 락 없이 동시 주문 후 재고: " + finalProduct.getStockQuantity());
        System.out.println("예상 재고: 0, 실제로는 정합성이 깨질 수 있음");
    }
}
