package com.hhplus.ecommerce.application.usecase;

import com.hhplus.ecommerce.domain.entity.Order;
import com.hhplus.ecommerce.domain.entity.User;
import com.hhplus.ecommerce.domain.exception.OrderNotFoundException;
import com.hhplus.ecommerce.domain.exception.UserNotFoundException;
import com.hhplus.ecommerce.domain.repository.OrderRepository;
import com.hhplus.ecommerce.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 결제 유스케이스
 * - 잔액 조회/충전, 결제 처리 비즈니스 로직
 * - 외부 전송 실패가 주문을 막지 않도록 처리
 * - Mock 환경에서 synchronized로 동시성 제어
 * - 실제 프로덕션에서는 @Transactional + JPA 비관적 락 사용 필요
 */
@Service
public class PaymentUseCase {

    private static final Logger log = LoggerFactory.getLogger(PaymentUseCase.class);

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public PaymentUseCase(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * 잔액 조회
     */
    public int getBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return user.getPoint();
    }

    /**
     * 잔액 충전
     * - synchronized로 동시성 제어 (Mock 테스트용)
     * - 실제 프로덕션에서는 @Transactional + JPA 비관적 락으로 교체 필요
     */
    public synchronized User chargeBalance(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.chargePoint(amount);
        return userRepository.save(user);
    }

    /**
     * 결제 실행
     * - 주문 금액 차감
     * - 주문 완료 처리
     * - 외부 전송은 비동기로 처리하거나 실패해도 주문은 성공
     * - synchronized로 동시성 제어 (Mock 테스트용)
     * - 실제 프로덕션에서는 @Transactional + JPA 비관적 락으로 교체 필요
     */
    public synchronized PaymentResult executePayment(Long userId, Long orderId) {
        // 1. 사용자 및 주문 조회
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        // 2. 결제 금액 차감
        int paymentAmount = order.getFinalAmount();
        user.deductPoint(paymentAmount);
        userRepository.save(user);

        // 3. 주문 완료 처리
        order.complete();
        orderRepository.save(order);

        // 4. 외부 전송 시도 (실패해도 주문은 성공으로 처리)
        boolean dataTransferSuccess = sendToExternalSystem(order);

        return new PaymentResult(
                orderId,
                userId,
                paymentAmount,
                user.getPoint(),
                dataTransferSuccess
        );
    }

    /**
     * 외부 시스템으로 데이터 전송
     * - 실패해도 예외를 던지지 않음
     * - 로그 기록 후 재시도 큐에 추가하는 방식으로 처리 가능
     */
    private boolean sendToExternalSystem(Order order) {
        try {
            // TODO: 실제 외부 API 호출 구현
            // externalApiClient.sendOrder(order);
            log.info("외부 시스템으로 주문 데이터 전송 성공: orderId={}", order.getId());
            return true;
        } catch (Exception e) {
            // 외부 전송 실패해도 주문은 성공
            log.error("외부 시스템으로 주문 데이터 전송 실패: orderId={}, error={}",
                    order.getId(), e.getMessage());
            // TODO: 재시도 큐에 추가
            return false;
        }
    }

    /**
     * 결제 결과 DTO
     */
    public record PaymentResult(
            Long orderId,
            Long userId,
            int paymentAmount,
            int remainingBalance,
            boolean dataTransferSuccess
    ) {}
}
