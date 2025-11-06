package com.hhplus.ecommerce.presentation.controller;

import com.hhplus.ecommerce.application.usecase.ProductUseCase;
import com.hhplus.ecommerce.common.ApiResponse;
import com.hhplus.ecommerce.domain.entity.Product;
import com.hhplus.ecommerce.presentation.dto.PaginationResponse;
import com.hhplus.ecommerce.presentation.dto.ProductListResponse;
import com.hhplus.ecommerce.presentation.dto.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Product", description = "상품 API")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @Operation(summary = "상품 목록 조회", description = "페이징 및 검색 조건으로 상품 목록을 조회합니다")
    @GetMapping
    public ApiResponse<ProductListResponse> getProducts(
            @Parameter(description = "페이지 번호", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "검색어") @RequestParam(required = false) String search) {

        List<Product> products = productUseCase.getProducts(page, size, search);
        long totalCount = productUseCase.getProductCount(search);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        List<ProductResponse> productResponses = products.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());

        PaginationResponse pagination = new PaginationResponse(page, size, totalCount, totalPages);
        ProductListResponse data = new ProductListResponse(productResponses, pagination);

        return ApiResponse.success(data);
    }

    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상품 상세 정보를 조회합니다")
    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProduct(
            @Parameter(description = "상품 ID", example = "1") @PathVariable Long productId) {

        Product product = productUseCase.getProduct(productId);
        ProductResponse productResponse = toProductResponse(product);

        return ApiResponse.success(productResponse);
    }

    @Operation(summary = "인기 상품 조회", description = "최근 3일간 판매량 기준 Top 5 상품을 조회합니다")
    @GetMapping("/popular")
    public ApiResponse<ProductListResponse> getPopularProducts() {
        List<Product> products = productUseCase.getTopProducts();

        List<ProductResponse> productResponses = products.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());

        ProductListResponse data = new ProductListResponse(productResponses, null);

        return ApiResponse.success(data, "최근 3일간 판매량 기준 Top 5");
    }

    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStockQuantity(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
