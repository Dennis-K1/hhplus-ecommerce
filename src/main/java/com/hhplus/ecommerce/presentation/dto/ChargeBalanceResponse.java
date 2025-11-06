package com.hhplus.ecommerce.presentation.dto;

public record ChargeBalanceResponse(
        Long userId,
        Integer previousBalance,
        Integer chargeAmount,
        Integer currentBalance
) {
}
