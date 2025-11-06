package com.hhplus.ecommerce.presentation.controller;

import com.hhplus.ecommerce.application.usecase.OrderUseCase;
import com.hhplus.ecommerce.common.ApiResponse;
import com.hhplus.ecommerce.domain.entity.Order;
import com.hhplus.ecommerce.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Order", description = "주문 API")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderUseCase orderUseCase;

    public OrderController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    @Operation(summary = "주문 생성", description = "장바구니 또는 즉시 구매로 주문을 생성합니다")
    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {

        // DTO 변환 - 단일 상품 주문
        List<OrderUseCase.OrderItem> orderItems = List.of(
                new OrderUseCase.OrderItem(request.productId(), request.quantity())
        );

        // 주문 생성 (쿠폰 미사용)
        Order order = orderUseCase.createOrder(
                request.userId(),
                orderItems,
                null
        );

        // Response 변환 (임시로 빈 리스트 사용)
        List<OrderItemResponse> items = Arrays.asList();

        OrderResponse data = new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getOrderStatus().name(),
                items,
                order.getTotalAmount(),
                order.getDiscountAmount(),
                LocalDateTime.now()
        );

        return ApiResponse.success(data, "주문이 생성되었습니다");
    }

    @Operation(summary = "주문 상세 조회", description = "주문 ID로 주문 상세 정보를 조회합니다")
    @GetMapping("/{orderId}")
    public ApiResponse<OrderDetailResponse> getOrder(
            @Parameter(description = "주문 ID", example = "100") @PathVariable Long orderId) {

        Order order = orderUseCase.getOrder(orderId);

        // Response 변환 (임시로 빈 리스트 사용)
        List<OrderItemResponse> items = Arrays.asList();

        OrderDetailResponse data = new OrderDetailResponse(
                order.getId(),
                order.getUserId(),
                order.getOrderStatus().name(),
                items,
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getUsedCouponId(),
                order.getFinalAmount(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        return ApiResponse.success(data);
    }

    @Operation(summary = "주문 목록 조회", description = "사용자의 주문 목록을 조회합니다")
    @GetMapping
    public ApiResponse<OrderListResponse> getOrders(
            @Parameter(description = "사용자 ID", example = "1") @RequestParam Long userId,
            @Parameter(description = "페이지 번호", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") Integer size) {

        List<Order> orders = orderUseCase.getOrders(userId, page, size);
        long totalCount = orderUseCase.getOrderCount(userId);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        List<OrderListItemResponse> orderResponses = orders.stream()
                .map(order -> new OrderListItemResponse(
                        order.getId(),
                        order.getOrderStatus().name(),
                        order.getTotalAmount(),
                        order.getDiscountAmount(),
                        order.getFinalAmount(),
                        LocalDateTime.now()
                ))
                .collect(Collectors.toList());

        PaginationResponse pagination = new PaginationResponse(page, size, totalCount, totalPages);
        OrderListResponse data = new OrderListResponse(orderResponses, pagination);

        return ApiResponse.success(data);
    }
}
