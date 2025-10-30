package com.hhplus.ecommerce.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("잔액 조회 - 성공")
    void getBalance_Success() throws Exception {
        mockMvc.perform(get("/api/users/{userId}/balance", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.balance").value(1000000));
    }

    @Test
    @DisplayName("잔액 충전 - 성공")
    void chargeBalance_Success() throws Exception {
        String requestBody = """
                {
                    "amount": 500000
                }
                """;

        mockMvc.perform(post("/api/users/{userId}/balance", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.previousBalance").value(1000000))
                .andExpect(jsonPath("$.data.chargeAmount").value(500000))
                .andExpect(jsonPath("$.data.currentBalance").value(1500000))
                .andExpect(jsonPath("$.message").value("잔액이 충전되었습니다"));
    }

    @Test
    @DisplayName("결제 실행 - 성공")
    void executePayment_Success() throws Exception {
        String requestBody = """
                {
                    "orderId": 100,
                    "userId": 1,
                    "userCouponId": 5
                }
                """;

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.paymentId").value(1))
                .andExpect(jsonPath("$.data.orderId").value(100))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.totalAmount").value(3000000))
                .andExpect(jsonPath("$.data.discountAmount").value(500000))
                .andExpect(jsonPath("$.data.finalAmount").value(2500000))
                .andExpect(jsonPath("$.data.remainingBalance").value(500000))
                .andExpect(jsonPath("$.data.orderStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.message").value("결제가 완료되었습니다"));
    }
}
