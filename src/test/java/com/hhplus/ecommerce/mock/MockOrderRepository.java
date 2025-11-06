package com.hhplus.ecommerce.mock;

import com.hhplus.ecommerce.domain.entity.Order;
import com.hhplus.ecommerce.domain.repository.OrderRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * OrderRepository Mock 구현
 * - 테스트용 인메모리 저장소
 * - 동시성 테스트를 위해 ConcurrentHashMap 사용
 */
public class MockOrderRepository implements OrderRepository {

    // 동시성 제어를 위한 ConcurrentHashMap
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();

    // 자동 증가 ID
    private final AtomicLong idGenerator = new AtomicLong(1L);

    public MockOrderRepository() {
        // 초기 테스트 데이터는 비워둠
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public List<Order> findByUserId(Long userId, int page, int size) {
        List<Order> userOrders = orders.values().stream()
                .filter(order -> order.getUserId() == userId)
                .sorted(Comparator.comparing(Order::getId).reversed())
                .collect(Collectors.toList());

        // 페이징 처리
        int start = (page - 1) * size;
        int end = Math.min(start + size, userOrders.size());

        if (start >= userOrders.size()) {
            return new ArrayList<>();
        }

        return userOrders.subList(start, end);
    }

    @Override
    public long countByUserId(Long userId) {
        return orders.values().stream()
                .filter(order -> order.getUserId() == userId)
                .count();
    }

    @Override
    public Order save(Order order) {
        // ID가 없으면 자동 생성
        if (order.getId() == 0) {
            long newId = idGenerator.getAndIncrement();
            Order newOrder = new Order(
                    newId,
                    order.getUserId(),
                    order.getOrderStatus(),
                    order.getTotalAmount(),
                    order.getDiscountAmount(),
                    order.getUsedCouponId()
            );
            orders.put(newId, newOrder);
            return newOrder;
        }

        orders.put(order.getId(), order);
        return order;
    }
}
