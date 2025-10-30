package com.hhplus.ecommerce.order;

import com.hhplus.ecommerce.common.ApiResponse;
import com.hhplus.ecommerce.order.dto.*;
import com.hhplus.ecommerce.product.dto.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Tag(name = "Order", description = "주문 API")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Operation(summary = "주문 생성", description = "장바구니 또는 즉시 구매로 주문을 생성합니다")
    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        List<OrderItemResponse> items = Arrays.asList(
                new OrderItemResponse(
                        1L,
                        10L,
                        "노트북",
                        2,
                        1500000
                )
        );

        OrderResponse data = new OrderResponse(
                100L,
                request.userId(),
                "PENDING",
                items,
                3000000,
                0,
                LocalDateTime.of(2025, 1, 1, 0, 0)
        );

        return ApiResponse.success(data, "주문이 생성되었습니다");
    }

    @Operation(summary = "주문 상세 조회", description = "주문 ID로 주문 상세 정보를 조회합니다")
    @GetMapping("/{orderId}")
    public ApiResponse<OrderDetailResponse> getOrder(
            @Parameter(description = "주문 ID", example = "100") @PathVariable Long orderId) {
        List<OrderItemResponse> items = Arrays.asList(
                new OrderItemResponse(
                        1L,
                        10L,
                        "노트북",
                        2,
                        1500000
                )
        );

        OrderDetailResponse data = new OrderDetailResponse(
                orderId,
                1L,
                "COMPLETED",
                items,
                3000000,
                500000,
                5L,
                2500000,
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 1, 1, 0, 5)
        );

        return ApiResponse.success(data);
    }

    @Operation(summary = "주문 목록 조회", description = "사용자의 주문 목록을 조회합니다")
    @GetMapping
    public ApiResponse<OrderListResponse> getOrders(
            @Parameter(description = "사용자 ID", example = "1") @RequestParam Long userId,
            @Parameter(description = "페이지 번호", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") Integer size) {

        List<OrderListItemResponse> orders = Arrays.asList(
                new OrderListItemResponse(
                        100L,
                        "COMPLETED",
                        3000000,
                        500000,
                        2500000,
                        LocalDateTime.of(2025, 1, 1, 0, 0)
                )
        );

        PaginationResponse pagination = new PaginationResponse(page, size, 10L, 1);
        OrderListResponse data = new OrderListResponse(orders, pagination);

        return ApiResponse.success(data);
    }
}
