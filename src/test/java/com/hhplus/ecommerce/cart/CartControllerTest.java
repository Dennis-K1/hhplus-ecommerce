package com.hhplus.ecommerce.cart;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("장바구니 조회 - 성공")
    void getCart_Success() throws Exception {
        mockMvc.perform(get("/api/users/{userId}/carts", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].cartItemId").value(1))
                .andExpect(jsonPath("$.data.items[0].product.productName").value("노트북"))
                .andExpect(jsonPath("$.data.items[0].quantity").value(2))
                .andExpect(jsonPath("$.data.items[0].amount").value(3000000))
                .andExpect(jsonPath("$.data.totalAmount").value(3000000));
    }

    @Test
    @DisplayName("장바구니 추가 - 성공")
    void addToCart_Success() throws Exception {
        String requestBody = """
                {
                    "productId": 10,
                    "quantity": 2
                }
                """;

        mockMvc.perform(post("/api/users/{userId}/carts", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cartItemId").value(1))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.productId").value(10))
                .andExpect(jsonPath("$.data.quantity").value(2))
                .andExpect(jsonPath("$.message").value("장바구니에 상품이 추가되었습니다"));
    }

    @Test
    @DisplayName("장바구니 수량 수정 - 성공")
    void updateQuantity_Success() throws Exception {
        String requestBody = """
                {
                    "quantity": 5
                }
                """;

        mockMvc.perform(patch("/api/users/{userId}/carts/{cartItemId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cartItemId").value(1))
                .andExpect(jsonPath("$.data.quantity").value(5))
                .andExpect(jsonPath("$.message").value("장바구니 수량이 수정되었습니다"));
    }

    @Test
    @DisplayName("장바구니 삭제 - 성공")
    void removeFromCart_Success() throws Exception {
        mockMvc.perform(delete("/api/users/{userId}/carts/{cartItemId}", 1L, 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("장바구니에서 상품이 제거되었습니다"));
    }
}
