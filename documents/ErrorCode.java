package com.hhplus.ecommerce.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // ========================================
    // COMMON (COM-xxx) - 공통 에러
    // ========================================
    INVALID_INPUT              ("COM-001", HttpStatus.BAD_REQUEST,            "잘못된 입력 값입니다"),
    INTERNAL_SERVER_ERROR      ("COM-999", HttpStatus.INTERNAL_SERVER_ERROR,  "서버 내부 오류가 발생했습니다"),

    // ========================================
    // USER (USR-xxx) - 사용자 관련ㅇ
    // ========================================
    USER_NOT_FOUND             ("USR-001", HttpStatus.NOT_FOUND,              "사용자를 찾을 수 없습니다"),
    // USR-002 ~ USR-099: 추가 사용자 에러 확장 가능

    // ========================================
    // PRODUCT (PRD-xxx) - 상품 관련
    // ========================================
    PRODUCT_NOT_FOUND          ("PRD-001", HttpStatus.NOT_FOUND,              "상품을 찾을 수 없습니다"),
    INSUFFICIENT_STOCK         ("PRD-002", HttpStatus.UNPROCESSABLE_ENTITY,   "재고가 부족합니다"),
    OUT_OF_STOCK               ("PRD-003", HttpStatus.UNPROCESSABLE_ENTITY,   "재고가 없습니다"),
    // PRD-004 ~ PRD-099: 추가 상품 에러 확장 가능

    // ========================================
    // CART (CRT-xxx) - 장바구니 관련
    // ========================================
    CART_ITEM_NOT_FOUND        ("CRT-001", HttpStatus.NOT_FOUND,              "장바구니 항목을 찾을 수 없습니다"),
    CART_EMPTY                 ("CRT-002", HttpStatus.UNPROCESSABLE_ENTITY,   "장바구니가 비어있습니다"),
    // CRT-003 ~ CRT-099: 추가 장바구니 에러 확장 가능

    // ========================================
    // ORDER (ORD-xxx) - 주문 관련
    // ========================================
    ORDER_NOT_FOUND            ("ORD-001", HttpStatus.NOT_FOUND,              "주문을 찾을 수 없습니다"),
    INVALID_ORDER_STATUS       ("ORD-002", HttpStatus.UNPROCESSABLE_ENTITY,   "유효하지 않은 주문 상태입니다"),
    ORDER_ALREADY_COMPLETED    ("ORD-003", HttpStatus.CONFLICT,               "이미 완료된 주문입니다"),
    ORDER_ALREADY_CANCELLED    ("ORD-004", HttpStatus.CONFLICT,               "이미 취소된 주문입니다"),
    // ORD-005 ~ ORD-099: 추가 주문 에러 확장 가능

    // ========================================
    // PAYMENT (PAY-xxx) - 결제 관련
    // ========================================
    INSUFFICIENT_BALANCE       ("PAY-001", HttpStatus.UNPROCESSABLE_ENTITY,   "잔액이 부족합니다"),
    // PAY-002 ~ PAY-099: 추가 결제 에러 확장 가능

    // ========================================
    // COUPON (CPN-xxx) - 쿠폰 관련
    // ========================================
    COUPON_NOT_FOUND           ("CPN-001", HttpStatus.NOT_FOUND,              "쿠폰을 찾을 수 없습니다"),
    USER_COUPON_NOT_FOUND      ("CPN-002", HttpStatus.NOT_FOUND,              "사용자 쿠폰을 찾을 수 없습니다"),
    COUPON_ALREADY_ISSUED      ("CPN-003", HttpStatus.CONFLICT,               "이미 발급받은 쿠폰입니다"),
    COUPON_SOLD_OUT            ("CPN-004", HttpStatus.UNPROCESSABLE_ENTITY,   "쿠폰 수량이 소진되었습니다"),
    COUPON_ALREADY_USED        ("CPN-005", HttpStatus.UNPROCESSABLE_ENTITY,   "이미 사용된 쿠폰입니다"),
    COUPON_EXPIRED             ("CPN-006", HttpStatus.UNPROCESSABLE_ENTITY,   "만료된 쿠폰입니다"),
    COUPON_NOT_OWNED           ("CPN-007", HttpStatus.UNPROCESSABLE_ENTITY,   "소유하지 않은 쿠폰입니다");
    // CPN-008 ~ CPN-099: 추가 쿠폰 에러 확장 가능

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return httpStatus.value();
    }
}
