package com.hhplus.ecommerce.payment.dto;

public record BalanceResponse(
        Long userId,
        Integer balance
) {
}
