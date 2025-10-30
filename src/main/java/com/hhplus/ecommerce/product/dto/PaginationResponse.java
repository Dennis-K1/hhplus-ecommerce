package com.hhplus.ecommerce.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaginationResponse {
    private Integer currentPage;
    private Integer pageSize;
    private Long totalItems;
    private Integer totalPages;
}
