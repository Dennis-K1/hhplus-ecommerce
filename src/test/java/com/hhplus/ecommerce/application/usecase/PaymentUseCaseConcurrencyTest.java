package com.hhplus.ecommerce.application.usecase;

import com.hhplus.ecommerce.domain.entity.Order;
import com.hhplus.ecommerce.domain.entity.OrderStatus;
import com.hhplus.ecommerce.domain.entity.User;
import com.hhplus.ecommerce.domain.exception.InsufficientBalanceException;
import com.hhplus.ecommerce.mock.MockOrderRepository;
import com.hhplus.ecommerce.mock.MockUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 결제 유스케이스 동시성 테스트
 * - 여러 스레드가 동시에 포인트를 사용할 때 잔액 정합성 검증
 */
class PaymentUseCaseConcurrencyTest {

    private MockUserRepository userRepository;
    private MockOrderRepository orderRepository;
    private PaymentUseCase paymentUseCase;

    @BeforeEach
    void setUp() {
        userRepository = new MockUserRepository();
        orderRepository = new MockOrderRepository();
        paymentUseCase = new PaymentUseCase(userRepository, orderRepository);
    }

    @Test
    @DisplayName("동시에 10개 주문 결제 - 잔액 100만원, 각 주문 10만원")
    void 동시_결제_포인트_차감() throws InterruptedException {
        // Given: 잔액 100만원인 사용자, 10만원짜리 주문 10개
        User user = new User(1L, 1000000);
        userRepository.save(user);

        // 10개의 주문 생성 (각 10만원)
        for (int i = 1; i <= 10; i++) {
            Order order = new Order((long) i, 1L, OrderStatus.PENDING, 100000, 0, 0);
            orderRepository.save(order);
        }

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);

        // When: 10개 주문을 동시에 결제
        for (int i = 1; i <= threadCount; i++) {
            long orderId = i;
            executorService.submit(() -> {
                try {
                    paymentUseCase.executePayment(1L, orderId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    fail("충분한 잔액이 있으므로 실패하면 안됨: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then: 모든 결제 성공
        assertEquals(10, successCount.get(), "모든 결제가 성공해야 함");

        // 최종 잔액 확인 (100만원 - 10만원*10 = 0)
        User finalUser = userRepository.findById(1L).orElseThrow();
        assertEquals(0, finalUser.getPoint(), "최종 잔액은 0이어야 함");
    }

    @Test
    @DisplayName("동시에 20번 충전 - 각 5만원씩")
    void 동시_포인트_충전() throws InterruptedException {
        // Given: 잔액 0원인 사용자
        User user = new User(1L, 0);
        userRepository.save(user);

        int threadCount = 20;
        int chargeAmount = 50000;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // When: 20번 동시에 5만원씩 충전
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    paymentUseCase.chargeBalance(1L, chargeAmount);
                } catch (Exception e) {
                    fail("충전 실패하면 안됨: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then: 최종 잔액은 100만원 (5만원 * 20)
        User finalUser = userRepository.findById(1L).orElseThrow();
        assertEquals(1000000, finalUser.getPoint(), "최종 잔액은 100만원이어야 함");
    }

    @Test
    @DisplayName("잔액 부족 시나리오 - 50만원으로 10만원짜리 주문 10개 결제")
    void 동시_결제_잔액_부족() throws InterruptedException {
        // Given: 잔액 50만원인 사용자
        User user = new User(1L, 500000);
        userRepository.save(user);

        // 10개의 주문 생성 (각 10만원)
        for (int i = 1; i <= 10; i++) {
            Order order = new Order((long) i, 1L, OrderStatus.PENDING, 100000, 0, 0);
            orderRepository.save(order);
        }

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When: 10개 주문을 동시에 결제 시도
        for (int i = 1; i <= threadCount; i++) {
            long orderId = i;
            executorService.submit(() -> {
                try {
                    paymentUseCase.executePayment(1L, orderId);
                    successCount.incrementAndGet();
                } catch (InsufficientBalanceException e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then: 5개만 성공, 5개는 실패
        assertEquals(5, successCount.get(), "잔액 50만원으로 5개만 결제 가능");
        assertEquals(5, failCount.get(), "나머지 5개는 잔액 부족으로 실패");

        // 최종 잔액 확인
        User finalUser = userRepository.findById(1L).orElseThrow();
        assertEquals(0, finalUser.getPoint(), "최종 잔액은 0이어야 함");
    }

    @Test
    @DisplayName("동시 충전과 동시 사용 - Race Condition 테스트")
    void 동시_충전과_사용() throws InterruptedException {
        // Given: 잔액 0원인 사용자
        User user = new User(1L, 0);
        userRepository.save(user);

        // 10개의 주문 생성 (각 5만원)
        for (int i = 1; i <= 10; i++) {
            Order order = new Order((long) i, 1L, OrderStatus.PENDING, 50000, 0, 0);
            orderRepository.save(order);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(20);

        // When: 10번 충전(각 5만원) + 10번 결제(각 5만원) 동시 실행
        // 충전 스레드
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                try {
                    paymentUseCase.chargeBalance(1L, 50000);
                } finally {
                    latch.countDown();
                }
            });
        }

        // 결제 스레드
        for (int i = 1; i <= 10; i++) {
            long orderId = i;
            executorService.submit(() -> {
                try {
                    paymentUseCase.executePayment(1L, orderId);
                } catch (InsufficientBalanceException e) {
                    // 잔액 부족은 발생할 수 있음 (타이밍에 따라)
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then: synchronized 덕분에 잔액이 음수가 되지 않고 정합성 유지
        User finalUser = userRepository.findById(1L).orElseThrow();
        assertTrue(finalUser.getPoint() >= 0, "잔액은 음수가 될 수 없음");
        assertTrue(finalUser.getPoint() <= 500000, "최대 잔액은 500,000원 (충전 총액)");

        // 충전 총액(500,000원)에서 결제 성공한 만큼 차감된 금액
        System.out.println("최종 잔액: " + finalUser.getPoint() + "원");
        System.out.println("충전 총액: 500,000원 (5만원 * 10번)");
        System.out.println("synchronized로 동시성 제어되어 잔액 정합성 유지됨");
    }
}
