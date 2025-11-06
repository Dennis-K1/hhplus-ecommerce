package com.hhplus.ecommerce.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.hhplus.ecommerce.presentation.controller.OrderController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("주문 생성 (장바구니) - 성공")
    void createOrder_Cart_Success() throws Exception {
        String requestBody = """
                {
                    "userId": 1,
                    "orderType": "CART"
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(100))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.orderStatus").value("PENDING"))
                .andExpect(jsonPath("$.data.items[0].productName").value("노트북"))
                .andExpect(jsonPath("$.data.totalAmount").value(3000000))
                .andExpect(jsonPath("$.message").value("주문이 생성되었습니다"));
    }

    @Test
    @DisplayName("주문 생성 (즉시 구매) - 성공")
    void createOrder_Direct_Success() throws Exception {
        String requestBody = """
                {
                    "userId": 1,
                    "orderType": "DIRECT",
                    "productId": 10,
                    "quantity": 1
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(100))
                .andExpect(jsonPath("$.data.orderStatus").value("PENDING"));
    }

    @Test
    @DisplayName("주문 상세 조회 - 성공")
    void getOrder_Success() throws Exception {
        mockMvc.perform(get("/api/orders/{orderId}", 100L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(100))
                .andExpect(jsonPath("$.data.orderStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.data.totalAmount").value(3000000))
                .andExpect(jsonPath("$.data.discountAmount").value(500000))
                .andExpect(jsonPath("$.data.finalAmount").value(2500000))
                .andExpect(jsonPath("$.data.usedCouponId").value(5));
    }

    @Test
    @DisplayName("주문 목록 조회 - 성공")
    void getOrders_Success() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .param("userId", "1")
                        .param("page", "1")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orders").isArray())
                .andExpect(jsonPath("$.data.orders[0].orderId").value(100))
                .andExpect(jsonPath("$.data.orders[0].orderStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.data.pagination.currentPage").value(1))
                .andExpect(jsonPath("$.data.pagination.totalItems").value(10));
    }
}
