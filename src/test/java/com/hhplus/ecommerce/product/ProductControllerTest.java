package com.hhplus.ecommerce.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.hhplus.ecommerce.presentation.controller.ProductController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("상품 목록 조회 - 성공")
    void getProducts_Success() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("page", "1")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.products").isArray())
                .andExpect(jsonPath("$.data.products[0].productId").value(1))
                .andExpect(jsonPath("$.data.products[0].productName").value("노트북"))
                .andExpect(jsonPath("$.data.pagination.currentPage").value(1))
                .andExpect(jsonPath("$.data.pagination.pageSize").value(20));
    }

    @Test
    @DisplayName("상품 상세 조회 - 성공")
    void getProduct_Success() throws Exception {
        mockMvc.perform(get("/api/products/{productId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productId").value(1))
                .andExpect(jsonPath("$.data.productName").value("노트북"))
                .andExpect(jsonPath("$.data.price").value(1500000))
                .andExpect(jsonPath("$.data.stockQuantity").value(50));
    }

    @Test
    @DisplayName("인기 상품 조회 - 성공")
    void getPopularProducts_Success() throws Exception {
        mockMvc.perform(get("/api/products/popular"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.products").isArray())
                .andExpect(jsonPath("$.data.products[0].productName").value("노트북"))
                .andExpect(jsonPath("$.message").value("최근 3일간 판매량 기준 Top 5"));
    }
}
