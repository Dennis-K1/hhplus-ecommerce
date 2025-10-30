package com.hhplus.ecommerce.payment;

import com.hhplus.ecommerce.common.ApiResponse;
import com.hhplus.ecommerce.payment.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Payment", description = "결제 API")
@RestController
public class PaymentController {

    @Operation(summary = "잔액 조회", description = "사용자의 잔액을 조회합니다")
    @GetMapping("/api/users/{userId}/balance")
    public ApiResponse<BalanceResponse> getBalance(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long userId) {
        BalanceResponse data = new BalanceResponse(userId, 1000000);
        return ApiResponse.success(data);
    }

    @Operation(summary = "잔액 충전", description = "사용자의 잔액을 충전합니다")
    @PostMapping("/api/users/{userId}/balance")
    public ApiResponse<ChargeBalanceResponse> chargeBalance(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long userId,
            @RequestBody ChargeBalanceRequest request) {

        ChargeBalanceResponse data = new ChargeBalanceResponse(
                userId,
                1000000,
                request.amount(),
                1000000 + request.amount()
        );

        return ApiResponse.success(data, "잔액이 충전되었습니다");
    }

    @Operation(summary = "결제 실행", description = "주문에 대한 결제를 실행합니다")
    @PostMapping("/api/payments")
    public ApiResponse<PaymentResponse> executePayment(@RequestBody ExecutePaymentRequest request) {
        PaymentResponse data = new PaymentResponse(
                1L,
                request.orderId(),
                request.userId(),
                3000000,
                500000,
                2500000,
                500000,
                "COMPLETED",
                LocalDateTime.of(2025, 1, 1, 0, 5)
        );

        return ApiResponse.success(data, "결제가 완료되었습니다");
    }
}
