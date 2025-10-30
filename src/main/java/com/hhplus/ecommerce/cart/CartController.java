package com.hhplus.ecommerce.cart;

import com.hhplus.ecommerce.cart.dto.*;
import com.hhplus.ecommerce.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Tag(name = "Cart", description = "장바구니 API")
@RestController
@RequestMapping("/api/users/{userId}/carts")
public class CartController {

    @Operation(summary = "장바구니 조회", description = "사용자의 장바구니 목록을 조회합니다")
    @GetMapping
    public ApiResponse<CartResponse> getCart(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long userId) {
        List<CartItemResponse> items = Arrays.asList(
                new CartItemResponse(
                        1L,
                        new ProductInfo(10L, "노트북", 1500000),
                        2,
                        3000000
                )
        );

        CartResponse data = new CartResponse(items, 3000000);
        return ApiResponse.success(data);
    }

    @Operation(summary = "장바구니 상품 추가", description = "장바구니에 상품을 추가합니다")
    @PostMapping
    public ApiResponse<AddToCartResponse> addToCart(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long userId,
            @RequestBody AddToCartRequest request) {

        AddToCartResponse data = new AddToCartResponse(
                1L,
                userId,
                request.productId(),
                request.quantity(),
                LocalDateTime.of(2025, 1, 1, 0, 0)
        );

        return ApiResponse.success(data, "장바구니에 상품이 추가되었습니다");
    }

    @Operation(summary = "장바구니 수량 수정", description = "장바구니 상품의 수량을 수정합니다")
    @PatchMapping("/{cartItemId}")
    public ApiResponse<UpdateCartResponse> updateQuantity(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long userId,
            @Parameter(description = "장바구니 아이템 ID", example = "1") @PathVariable Long cartItemId,
            @RequestBody UpdateCartQuantityRequest request) {

        UpdateCartResponse data = new UpdateCartResponse(
                cartItemId,
                userId,
                10L,
                request.quantity(),
                LocalDateTime.of(2025, 1, 1, 1, 0)
        );

        return ApiResponse.success(data, "장바구니 수량이 수정되었습니다");
    }

    @Operation(summary = "장바구니 상품 삭제", description = "장바구니에서 상품을 삭제합니다")
    @DeleteMapping("/{cartItemId}")
    public ApiResponse<Void> removeFromCart(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long userId,
            @Parameter(description = "장바구니 아이템 ID", example = "1") @PathVariable Long cartItemId) {

        return ApiResponse.success(null, "장바구니에서 상품이 제거되었습니다");
    }
}
