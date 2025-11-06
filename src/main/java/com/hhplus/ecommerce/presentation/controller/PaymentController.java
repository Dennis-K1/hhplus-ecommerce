package com.hhplus.ecommerce.presentation.controller;

import com.hhplus.ecommerce.application.usecase.PaymentUseCase;
import com.hhplus.ecommerce.common.ApiResponse;
import com.hhplus.ecommerce.domain.entity.User;
import com.hhplus.ecommerce.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Payment", description = "결제 API")
@RestController
public class PaymentController {

    private final PaymentUseCase paymentUseCase;

    public PaymentController(PaymentUseCase paymentUseCase) {
        this.paymentUseCase = paymentUseCase;
    }

    @Operation(summary = "잔액 조회", description = "사용자의 잔액을 조회합니다")
    @GetMapping("/api/users/{userId}/balance")
    public ApiResponse<BalanceResponse> getBalance(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long userId) {

        int balance = paymentUseCase.getBalance(userId);
        BalanceResponse data = new BalanceResponse(userId, balance);

        return ApiResponse.success(data);
    }

    @Operation(summary = "잔액 충전", description = "사용자의 잔액을 충전합니다")
    @PostMapping("/api/users/{userId}/balance")
    public ApiResponse<ChargeBalanceResponse> chargeBalance(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long userId,
            @RequestBody ChargeBalanceRequest request) {

        int beforeBalance = paymentUseCase.getBalance(userId);
        User user = paymentUseCase.chargeBalance(userId, request.amount());

        ChargeBalanceResponse data = new ChargeBalanceResponse(
                userId,
                beforeBalance,
                request.amount(),
                user.getPoint()
        );

        return ApiResponse.success(data, "잔액이 충전되었습니다");
    }

    @Operation(summary = "결제 실행", description = "주문에 대한 결제를 실행합니다")
    @PostMapping("/api/payments")
    public ApiResponse<PaymentResponse> executePayment(@RequestBody ExecutePaymentRequest request) {

        PaymentUseCase.PaymentResult result = paymentUseCase.executePayment(
                request.userId(),
                request.orderId()
        );

        PaymentResponse data = new PaymentResponse(
                1L,  // paymentId (임시)
                result.orderId(),
                result.userId(),
                result.paymentAmount(),
                0,  // discount (임시)
                result.paymentAmount(),
                result.remainingBalance(),
                "COMPLETED",
                LocalDateTime.now()
        );

        String message = result.dataTransferSuccess()
                ? "결제가 완료되었습니다"
                : "결제가 완료되었습니다 (외부 전송은 재시도 예정)";

        return ApiResponse.success(data, message);
    }
}
