package com.hhplus.ecommerce.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("사용자 쿠폰 조회 - 성공")
    void getUserCoupons_Success() throws Exception {
        mockMvc.perform(get("/api/users/{userId}/coupons", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.coupons").isArray())
                .andExpect(jsonPath("$.data.coupons[0].userCouponId").value(1))
                .andExpect(jsonPath("$.data.coupons[0].coupon.couponName").value("신규가입 할인쿠폰"))
                .andExpect(jsonPath("$.data.coupons[0].coupon.discountAmount").value(10000))
                .andExpect(jsonPath("$.data.coupons[0].isUsed").value(false));
    }

    @Test
    @DisplayName("쿠폰 발급 - 성공")
    void issueCoupon_Success() throws Exception {
        String requestBody = """
                {
                    "couponId": 5
                }
                """;

        mockMvc.perform(post("/api/users/{userId}/coupons", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userCouponId").value(1))
                .andExpect(jsonPath("$.data.coupon.couponId").value(5))
                .andExpect(jsonPath("$.data.coupon.couponName").value("신규가입 할인쿠폰"))
                .andExpect(jsonPath("$.data.coupon.discountAmount").value(10000))
                .andExpect(jsonPath("$.data.isUsed").value(false))
                .andExpect(jsonPath("$.message").value("쿠폰이 발급되었습니다"));
    }
}
