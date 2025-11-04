package com.hhplus.ecommerce.presentation.dto;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long paymentId,
        Long orderId,
        Long userId,
        Integer totalAmount,
        Integer discountAmount,
        Integer finalAmount,
        Integer remainingBalance,
        String orderStatus,
        LocalDateTime createdAt
) {
}
