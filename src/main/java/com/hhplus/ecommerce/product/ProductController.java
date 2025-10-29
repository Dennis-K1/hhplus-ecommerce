package com.hhplus.ecommerce.product;

import com.hhplus.ecommerce.common.ApiResponse;
import com.hhplus.ecommerce.product.dto.PaginationResponse;
import com.hhplus.ecommerce.product.dto.ProductListResponse;
import com.hhplus.ecommerce.product.dto.ProductResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    public ApiResponse<ProductListResponse> getProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String search) {

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

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long productId) {
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
