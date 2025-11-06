package com.hhplus.ecommerce.domain.repository;

import com.hhplus.ecommerce.domain.entity.Order;

import java.util.List;
import java.util.Optional;

/**
 * 주문 리포지토리 인터페이스
 * - 도메인 레이어에 위치하여 인프라스트럭처 독립성 유지
 */
public interface OrderRepository {

    /**
     * 주문 조회
     */
    Optional<Order> findById(Long orderId);

    /**
     * 사용자의 주문 목록 조회
     */
    List<Order> findByUserId(Long userId, int page, int size);

    /**
     * 사용자의 전체 주문 수 조회
     */
    long countByUserId(Long userId);

    /**
     * 주문 저장
     */
    Order save(Order order);
}
