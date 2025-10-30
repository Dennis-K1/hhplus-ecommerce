package com.hhplus.ecommerce.payment.dto;

public record ChargeBalanceResponse(
        Long userId,
        Integer previousBalance,
        Integer chargeAmount,
        Integer currentBalance
) {
}
