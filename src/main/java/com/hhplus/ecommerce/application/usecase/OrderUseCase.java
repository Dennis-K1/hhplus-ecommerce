package com.hhplus.ecommerce.application.usecase;

import com.hhplus.ecommerce.domain.entity.Order;
import com.hhplus.ecommerce.domain.entity.OrderStatus;
import com.hhplus.ecommerce.domain.entity.Product;
import com.hhplus.ecommerce.domain.exception.OrderNotFoundException;
import com.hhplus.ecommerce.domain.exception.ProductNotFoundException;
import com.hhplus.ecommerce.domain.repository.OrderRepository;
import com.hhplus.ecommerce.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 주문 유스케이스
 * - 주문 생성 및 조회 비즈니스 로직
 * - 재고 차감은 트랜잭션으로 처리
 */
@Service
@Transactional(readOnly = true)
public class OrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderUseCase(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    /**
     * 주문 생성 (재고 차감 포함)
     * - 비관적 락을 사용하여 동시성 제어
     * - 트랜잭션으로 재고 차감과 주문 생성을 원자적으로 처리
     */
    @Transactional
    public Order createOrder(Long userId, List<OrderItem> items, Long couponId) {
        // 총 금액 계산 및 재고 차감 (비관적 락 사용)
        int totalAmount = 0;
        for (OrderItem item : items) {
            Product product = productRepository.findByIdWithLock(item.productId())
                    .orElseThrow(ProductNotFoundException::new);

            // 재고 차감 (도메인 로직)
            product.decreaseStock(item.quantity());
            productRepository.save(product);

            totalAmount += product.getPrice() * item.quantity();
        }

        // 주문 생성
        Order order = new Order(
                generateOrderId(),
                userId,
                OrderStatus.PENDING,
                totalAmount,
                0,
                couponId != null ? couponId : 0
        );

        return orderRepository.save(order);
    }

    /**
     * 주문 상세 조회
     */
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
    }

    /**
     * 사용자의 주문 목록 조회
     */
    public List<Order> getOrders(Long userId, int page, int size) {
        return orderRepository.findByUserId(userId, page, size);
    }

    /**
     * 사용자의 주문 전체 개수 조회
     */
    public long getOrderCount(Long userId) {
        return orderRepository.countByUserId(userId);
    }

    /**
     * 주문 완료 처리
     */
    @Transactional
    public Order completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        order.complete();
        return orderRepository.save(order);
    }

    /**
     * 주문 ID 생성 (임시)
     */
    private long generateOrderId() {
        return System.currentTimeMillis();
    }

    /**
     * 주문 아이템 DTO
     */
    public record OrderItem(Long productId, int quantity) {}
}
