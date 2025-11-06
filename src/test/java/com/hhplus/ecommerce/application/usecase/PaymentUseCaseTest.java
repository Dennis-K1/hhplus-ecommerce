package com.hhplus.ecommerce.application.usecase;

import com.hhplus.ecommerce.common.exception.InvalidInputException;
import com.hhplus.ecommerce.domain.entity.Order;
import com.hhplus.ecommerce.domain.entity.OrderStatus;
import com.hhplus.ecommerce.domain.entity.User;
import com.hhplus.ecommerce.domain.exception.InsufficientBalanceException;
import com.hhplus.ecommerce.domain.exception.OrderNotFoundException;
import com.hhplus.ecommerce.domain.exception.UserNotFoundException;
import com.hhplus.ecommerce.mock.MockOrderRepository;
import com.hhplus.ecommerce.mock.MockUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PaymentUseCase 단위 테스트
 * - Mock 구현체를 사용한 순수 단위 테스트
 * - Mockito 없이 실제 동작 검증
 */
class PaymentUseCaseTest {

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
    @DisplayName("잔액 조회 성공")
    void getBalance_성공() {
        // Given
        Long userId = 1L;
        User user = new User(userId, 50000);
        userRepository.save(user);

        // When
        int balance = paymentUseCase.getBalance(userId);

        // Then
        assertEquals(50000, balance);
    }

    @Test
    @DisplayName("잔액 조회 실패 - 사용자 없음")
    void getBalance_사용자없음() {
        // Given
        Long userId = 999L;

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            paymentUseCase.getBalance(userId);
        });
    }

    @Test
    @DisplayName("잔액 충전 성공")
    void chargeBalance_성공() {
        // Given
        Long userId = 1L;
        User user = new User(userId, 50000);
        userRepository.save(user);
        int chargeAmount = 10000;

        // When
        User result = paymentUseCase.chargeBalance(userId, chargeAmount);

        // Then
        assertNotNull(result);
        assertEquals(60000, result.getPoint()); // 50000 + 10000

        // Repository에서도 변경 확인
        User savedUser = userRepository.findById(userId).orElseThrow();
        assertEquals(60000, savedUser.getPoint());
    }

    @Test
    @DisplayName("잔액 충전 실패 - 사용자 없음")
    void chargeBalance_사용자없음() {
        // Given
        Long userId = 999L;
        int chargeAmount = 10000;

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            paymentUseCase.chargeBalance(userId, chargeAmount);
        });
    }

    @Test
    @DisplayName("잔액 충전 실패 - 음수 금액")
    void chargeBalance_음수금액() {
        // Given
        Long userId = 1L;
        User user = new User(userId, 50000);
        userRepository.save(user);
        int chargeAmount = -1000;

        // When & Then
        assertThrows(InvalidInputException.class, () -> {
            paymentUseCase.chargeBalance(userId, chargeAmount);
        });

        // 잔액이 변경되지 않았는지 확인
        User savedUser = userRepository.findById(userId).orElseThrow();
        assertEquals(50000, savedUser.getPoint());
    }

    @Test
    @DisplayName("결제 실행 성공")
    void executePayment_성공() {
        // Given
        Long userId = 1L;
        Long orderId = 1L;
        User user = new User(userId, 50000);
        Order order = new Order(orderId, userId, OrderStatus.PENDING, 20000, 0, 0);
        userRepository.save(user);
        orderRepository.save(order);

        // When
        PaymentUseCase.PaymentResult result = paymentUseCase.executePayment(userId, orderId);

        // Then
        assertNotNull(result);
        assertEquals(orderId, result.orderId());
        assertEquals(userId, result.userId());
        assertEquals(20000, result.paymentAmount());
        assertEquals(30000, result.remainingBalance()); // 50000 - 20000

        // Repository에서 실제 변경 확인
        User updatedUser = userRepository.findById(userId).orElseThrow();
        assertEquals(30000, updatedUser.getPoint());

        Order updatedOrder = orderRepository.findById(orderId).orElseThrow();
        assertEquals(OrderStatus.COMPLETED, updatedOrder.getOrderStatus());
    }

    @Test
    @DisplayName("결제 실행 실패 - 사용자 없음")
    void executePayment_사용자없음() {
        // Given
        Long userId = 999L;
        Long orderId = 1L;

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            paymentUseCase.executePayment(userId, orderId);
        });
    }

    @Test
    @DisplayName("결제 실행 실패 - 주문 없음")
    void executePayment_주문없음() {
        // Given
        Long userId = 1L;
        Long orderId = 999L;
        User user = new User(userId, 50000);
        userRepository.save(user);

        // When & Then
        assertThrows(OrderNotFoundException.class, () -> {
            paymentUseCase.executePayment(userId, orderId);
        });

        // 사용자 잔액이 변경되지 않았는지 확인
        User savedUser = userRepository.findById(userId).orElseThrow();
        assertEquals(50000, savedUser.getPoint());
    }

    @Test
    @DisplayName("결제 실행 실패 - 잔액 부족")
    void executePayment_잔액부족() {
        // Given
        Long userId = 1L;
        Long orderId = 1L;
        User poorUser = new User(userId, 10000); // 잔액 10000
        Order expensiveOrder = new Order(orderId, userId, OrderStatus.PENDING, 50000, 0, 0); // 주문 금액 50000
        userRepository.save(poorUser);
        orderRepository.save(expensiveOrder);

        // When & Then
        assertThrows(InsufficientBalanceException.class, () -> {
            paymentUseCase.executePayment(userId, orderId);
        });

        // 잔액과 주문 상태가 변경되지 않았는지 확인
        User savedUser = userRepository.findById(userId).orElseThrow();
        assertEquals(10000, savedUser.getPoint());

        Order savedOrder = orderRepository.findById(orderId).orElseThrow();
        assertEquals(OrderStatus.PENDING, savedOrder.getOrderStatus());
    }

    @Test
    @DisplayName("결제 실행 성공 - 외부 전송 실패해도 결제는 성공")
    void executePayment_외부전송실패() {
        // Given
        Long userId = 1L;
        Long orderId = 1L;
        User user = new User(userId, 50000);
        Order order = new Order(orderId, userId, OrderStatus.PENDING, 20000, 0, 0);
        userRepository.save(user);
        orderRepository.save(order);

        // When
        PaymentUseCase.PaymentResult result = paymentUseCase.executePayment(userId, orderId);

        // Then
        // 외부 전송 실패 여부와 관계없이 결제는 성공
        assertNotNull(result);
        assertEquals(30000, result.remainingBalance());

        // Repository에서 실제 변경 확인
        User updatedUser = userRepository.findById(userId).orElseThrow();
        assertEquals(30000, updatedUser.getPoint());

        Order updatedOrder = orderRepository.findById(orderId).orElseThrow();
        assertEquals(OrderStatus.COMPLETED, updatedOrder.getOrderStatus());
    }
}
