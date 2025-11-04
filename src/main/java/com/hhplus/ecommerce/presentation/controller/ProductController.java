package com.hhplus.ecommerce.presentation.controller;

import com.hhplus.ecommerce.common.ApiResponse;
import com.hhplus.ecommerce.presentation.dto.PaginationResponse;
import com.hhplus.ecommerce.presentation.dto.ProductListResponse;
import com.hhplus.ecommerce.presentation.dto.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Tag(name = "Product", description = "상품 API")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Operation(summary = "상품 목록 조회", description = "페이징 및 검색 조건으로 상품 목록을 조회합니다")
    @GetMapping
    public ApiResponse<ProductListResponse> getProducts(
            @Parameter(description = "페이지 번호", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "검색어") @RequestParam(required = false) String search) {

        List<ProductResponse> products = Arrays.asList(
                new ProductResponse(1L, "노트북", 1500000, 50,
                        LocalDateTime.of(2025, 1, 1, 0, 0),
                        LocalDateTime.of(2025, 1, 1, 0, 0)),
                new ProductResponse(2L, "마우스", 50000, 200,
                        LocalDateTime.of(2025, 1, 1, 0, 0),
                        LocalDateTime.of(2025, 1, 1, 0, 0))
        );

        PaginationResponse pagination = new PaginationResponse(page, size, 100L, 5);
        ProductListResponse data = new ProductListResponse(products, pagination);

        return ApiResponse.success(data);
    }

    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상품 상세 정보를 조회합니다")
    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProduct(
            @Parameter(description = "상품 ID", example = "1") @PathVariable Long productId) {
        ProductResponse product = new ProductResponse(
                productId,
                "노트북",
                1500000,
                50,
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 1, 1, 0, 0)
        );

        return ApiResponse.success(product);
    }

    @Operation(summary = "인기 상품 조회", description = "최근 3일간 판매량 기준 Top 5 상품을 조회합니다")
    @GetMapping("/popular")
    public ApiResponse<ProductListResponse> getPopularProducts() {
        List<ProductResponse> products = Arrays.asList(
                new ProductResponse(1L, "노트북", 1500000, 50,
                        LocalDateTime.of(2025, 1, 1, 0, 0),
                        LocalDateTime.of(2025, 1, 1, 0, 0)),
                new ProductResponse(2L, "마우스", 50000, 200,
                        LocalDateTime.of(2025, 1, 1, 0, 0),
                        LocalDateTime.of(2025, 1, 1, 0, 0))
        );

        ProductListResponse data = new ProductListResponse(products, null);

        return ApiResponse.success(data, "최근 3일간 판매량 기준 Top 5");
    }
}
