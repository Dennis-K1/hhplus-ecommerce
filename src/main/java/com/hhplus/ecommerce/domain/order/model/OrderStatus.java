package com.hhplus.ecommerce.domain.order.model;

/**
 * 주문 상태 enum
 */
public enum OrderStatus {
    PENDING,    // 결제 대기
    COMPLETED,  // 결제 완료
    CANCELLED   // 주문 취소
}
