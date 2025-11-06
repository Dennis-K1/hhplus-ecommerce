package com.hhplus.ecommerce.presentation.dto;

import java.util.List;

public record OrderListResponse(
        List<OrderListItemResponse> orders,
        PaginationResponse pagination
) {
}
