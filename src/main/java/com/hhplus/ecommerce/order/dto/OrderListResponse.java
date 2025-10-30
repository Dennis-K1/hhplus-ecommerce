package com.hhplus.ecommerce.order.dto;

import com.hhplus.ecommerce.product.dto.PaginationResponse;

import java.util.List;

public record OrderListResponse(
        List<OrderListItemResponse> orders,
        PaginationResponse pagination
) {
}
