package com.hhplus.ecommerce.application.usecase;

import com.hhplus.ecommerce.domain.entity.Coupon;
import com.hhplus.ecommerce.domain.entity.UserCoupon;
import com.hhplus.ecommerce.domain.exception.CouponSoldOutException;
import com.hhplus.ecommerce.mock.MockCouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 쿠폰 유스케이스 동시성 테스트
 * - 제한된 수량의 쿠폰을 여러 사용자가 동시에 발급받을 때 정합성 검증
 */
class CouponUseCaseConcurrencyTest {

    private MockCouponRepository couponRepository;
    private CouponUseCase couponUseCase;

    @BeforeEach
    void setUp() {
        couponRepository = new MockCouponRepository();
        couponUseCase = new CouponUseCase(couponRepository);
    }

    @Test
    @DisplayName("선착순 쿠폰 발급 - 100명이 10개 한정 쿠폰 발급 시도")
    void 선착순_쿠폰_발급() throws InterruptedException {
        // Given: 10개 한정 쿠폰
        LocalDateTime now = LocalDateTime.now();
        Coupon limitedCoupon = new Coupon(
                1L,
                10000,
                10,     // 총 10개
                0,      // 아직 발급 안됨
                now.minusDays(1),
                now.plusDays(30)
        );
        couponRepository.saveCoupon(limitedCoupon);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When: 100명이 동시에 쿠폰 발급 시도
        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1L;
            executorService.submit(() -> {
                try {
                    couponUseCase.issueCoupon(userId, 1L);
                    successCount.incrementAndGet();
                } catch (CouponSoldOutException e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then: 정확히 10명만 성공
        assertEquals(10, successCount.get(), "10개 한정이므로 10명만 성공해야 함");
        assertEquals(90, failCount.get(), "나머지 90명은 실패해야 함");

        // 쿠폰 발급 수량 확인 - Coupon 엔티티의 issuedQuantity 검증
        Coupon finalCoupon = couponRepository.findById(1L).orElseThrow();
        assertEquals(10, finalCoupon.getIssuedQuantity(),
                "발급된 수량은 정확히 10개여야 함 (동시성 제어 성공)");
    }

    @Test
    @DisplayName("여러 쿠폰을 동시에 발급 - 쿠폰별로 독립적으로 처리")
    void 여러_쿠폰_동시_발급() throws InterruptedException {
        // Given: 3개의 쿠폰 (각각 10개 한정)
        LocalDateTime now = LocalDateTime.now();
        for (long i = 1; i <= 3; i++) {
            Coupon coupon = new Coupon(
                    i,
                    10000 * (int)i,
                    10,
                    0,
                    now.minusDays(1),
                    now.plusDays(30)
            );
            couponRepository.saveCoupon(coupon);
        }

        int threadCount = 30; // 각 쿠폰당 10명씩
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);

        // When: 30명이 3개 쿠폰을 동시에 발급 (각 10명씩)
        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1L;
            long couponId = (i % 3) + 1; // 1, 2, 3 순환

            executorService.submit(() -> {
                try {
                    couponUseCase.issueCoupon(userId, couponId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    fail("충분한 수량이 있으므로 실패하면 안됨: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then: 모든 발급 성공
        assertEquals(30, successCount.get(), "30명 모두 성공해야 함");

        // 각 쿠폰의 발급 수량 확인
        for (long i = 1; i <= 3; i++) {
            Coupon coupon = couponRepository.findById(i).orElseThrow();
            assertEquals(10, coupon.getIssuedQuantity(),
                    "쿠폰 " + i + "는 10개가 발급되어야 함");
        }
    }

    @Test
    @DisplayName("쿠폰 발급 중 일부 실패 - 50개 중 30개만 발급")
    void 쿠폰_발급_일부_성공() throws InterruptedException {
        // Given: 30개 한정 쿠폰
        LocalDateTime now = LocalDateTime.now();
        Coupon coupon = new Coupon(1L, 5000, 30, 0,
                now.minusDays(1), now.plusDays(30));
        couponRepository.saveCoupon(coupon);

        int threadCount = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When: 50명이 동시에 발급 시도
        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1L;
            executorService.submit(() -> {
                try {
                    couponUseCase.issueCoupon(userId, 1L);
                    successCount.incrementAndGet();
                } catch (CouponSoldOutException e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        assertEquals(30, successCount.get(), "30개만 성공");
        assertEquals(20, failCount.get(), "20개는 실패");

        Coupon finalCoupon = couponRepository.findById(1L).orElseThrow();
        assertEquals(30, finalCoupon.getIssuedQuantity());
        assertEquals(30, finalCoupon.getIssueQuantity());
    }

    @Test
    @DisplayName("동시성 제어 검증 - Repository의 synchronized가 제대로 동작하는지 확인")
    void 동시성_제어_검증() throws InterruptedException {
        // Given: 10개 한정 쿠폰
        LocalDateTime now = LocalDateTime.now();
        Coupon coupon = new Coupon(1L, 10000, 10, 0,
                now.minusDays(1), now.plusDays(30));
        couponRepository.saveCoupon(coupon);

        int threadCount = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);

        // When: MockCouponRepository의 synchronized 메서드로 동시 발급
        // MockCouponRepository.findByIdForUpdate()는 synchronized로 동시성 제어
        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1L;
            executorService.submit(() -> {
                try {
                    // Repository의 synchronized 메서드를 통해 안전하게 조회/수정
                    Coupon c = couponRepository.findByIdForUpdate(1L).orElseThrow();

                    if (c.hasIssueQuantity()) {
                        c.issue(now);
                        couponRepository.saveCoupon(c);
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    // 수량 부족은 정상
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then: synchronized 덕분에 정확히 10개만 발급됨
        Coupon finalCoupon = couponRepository.findById(1L).orElseThrow();
        assertEquals(10, finalCoupon.getIssuedQuantity(),
                "synchronized로 동시성 제어되어 정확히 10개만 발급되어야 함");
        assertEquals(10, successCount.get(),
                "10번만 성공해야 함");
    }
}
