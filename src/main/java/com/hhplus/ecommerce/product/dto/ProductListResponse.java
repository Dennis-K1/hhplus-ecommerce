package com.hhplus.ecommerce.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProductListResponse {
    private List<ProductResponse> products;
    private PaginationResponse pagination;
}
