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

        // 쿠폰 발급 수량 확인
        Coupon finalCoupon = couponRepository.findById(1L).orElseThrow();
        assertEquals(10, finalCoupon.getIssuedQuantity(), "발급된 수량은 10개여야 함");

        // 중복 발급 확인
        List<UserCoupon> issuedCoupons = couponRepository.findByUserId(1L);
        List<Long> userIds = issuedCoupons.stream()
                .map(UserCoupon::getUserId)
                .collect(Collectors.toList());

        // 모든 userId가 고유해야 함 (중복 발급 없음)
        assertEquals(userIds.size(), userIds.stream().distinct().count(),
                "중복 발급이 없어야 함");
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
    @DisplayName("비관적 락의 중요성 - 락 없이는 Over-Issuing 발생 가능")
    void 비관적락_없이_쿠폰_발급() throws InterruptedException {
        // Given: 10개 한정 쿠폰
        LocalDateTime now = LocalDateTime.now();
        Coupon coupon = new Coupon(1L, 10000, 10, 0,
                now.minusDays(1), now.plusDays(30));
        couponRepository.saveCoupon(coupon);

        int threadCount = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // When: 비관적 락 없이 동시 발급 (findById 사용)
        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1L;
            executorService.submit(() -> {
                try {
                    // ❌ synchronized 없이 findById 사용 (동시성 문제)
                    Coupon c = couponRepository.findById(1L).orElseThrow();

                    if (c.hasIssueQuantity()) {
                        c.issue(LocalDateTime.now());
                        couponRepository.saveCoupon(c);
                    }
                } catch (Exception e) {
                    // 예외 무시
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then: 동시성 제어가 없으면 Over-Issuing 발생 가능
        Coupon finalCoupon = couponRepository.findById(1L).orElseThrow();
        System.out.println("동시성 제어 없이 발급된 수량: " + finalCoupon.getIssuedQuantity());
        System.out.println("기대값: 10, 실제로는 동시성 문제로 Over-Issuing 발생 가능");

        // 실제 유스케이스에서는 synchronized 메서드를 사용해야 함!
    }
}
