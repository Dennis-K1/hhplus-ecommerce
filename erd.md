# 이커머스 서비스 ERD

## 엔티티 관계도

```
USER (사용자)
├── CART_ITEM (1:N)
├── ORDER (1:N)
└── USER_COUPON (1:N)

PRODUCT (상품)
├── CART_ITEM (1:N)
└── ORDER_ITEM (1:N)

COUPON (쿠폰)
└── USER_COUPON (1:N)

ORDER (주문)
└── ORDER_ITEM (1:N)
```

---

## 테이블 상세

### USER (사용자)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| user_id | BIGINT | PK, AUTO_INCREMENT | 사용자 ID |
| user_name | VARCHAR(100) | NOT NULL | 사용자명 |
| point | DECIMAL(15,2) | NOT NULL, DEFAULT 0 | 포인트 잔액 |
| created_at | DATETIME | NOT NULL | 생성일시 |
| updated_at | DATETIME | NOT NULL | 수정일시 |

---

### PRODUCT (상품)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| product_id | BIGINT | PK, AUTO_INCREMENT | 상품 ID |
| product_name | VARCHAR(200) | NOT NULL | 상품명 |
| price | INT | NOT NULL | 가격 |
| stock_quantity | INT | NOT NULL, DEFAULT 0 | 재고 수량 |
| created_at | DATETIME | NOT NULL | 생성일시 |
| updated_at | DATETIME | NOT NULL | 수정일시 |

**INDEX**: `idx_product_name` (product_name)

---

### CART_ITEM (장바구니 항목)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| cart_item_id | BIGINT | PK, AUTO_INCREMENT | 장바구니 항목 ID |
| user_id | BIGINT | NOT NULL | 사용자 ID |
| product_id | BIGINT | NOT NULL | 상품 ID |
| quantity | INT | NOT NULL | 수량 |
| created_at | DATETIME | NOT NULL | 생성일시 |
| updated_at | DATETIME | NOT NULL | 수정일시 |

**UNIQUE KEY**: (user_id, product_id)
**INDEX**: `idx_user_id` (user_id)

---

### ORDER (주문)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| order_id | BIGINT | PK, AUTO_INCREMENT | 주문 ID |
| user_id | BIGINT | NOT NULL | 사용자 ID |
| order_status | VARCHAR(20) | NOT NULL | 주문 상태 |
| total_amount | DECIMAL(15,2) | NOT NULL | 총 주문 금액 |
| discount_amount | DECIMAL(15,2) | NOT NULL, DEFAULT 0 | 할인 금액 |
| used_coupon_id | BIGINT | NULL | 사용한 쿠폰 ID |
| created_at | DATETIME | NOT NULL | 생성일시 |
| updated_at | DATETIME | NOT NULL | 수정일시 |

**ORDER_STATUS**: PENDING, COMPLETED, CANCELLED

**INDEX**: `idx_user_created` (user_id, created_at DESC), `idx_created_status` (created_at DESC, order_status)

---

### ORDER_ITEM (주문 항목)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| order_item_id | BIGINT | PK, AUTO_INCREMENT | 주문 항목 ID |
| order_id | BIGINT | NOT NULL | 주문 ID |
| product_id | BIGINT | NOT NULL | 상품 ID |
| quantity | INT | NOT NULL | 수량 |
| created_at | DATETIME | NOT NULL | 생성일시 |

**INDEX**: `idx_order_id` (order_id)

---

### COUPON (쿠폰)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| coupon_id | BIGINT | PK, AUTO_INCREMENT | 쿠폰 ID |
| coupon_name | VARCHAR(100) | NOT NULL | 쿠폰명 |
| discount_amount | DECIMAL(15,2) | NOT NULL | 할인 금액 |
| issue_quantity | INT | NOT NULL | 발급 수량 |
| issued_quantity | INT | NOT NULL, DEFAULT 0 | 발급된 수량 |
| valid_from | DATETIME | NOT NULL | 유효 시작일 |
| valid_to | DATETIME | NOT NULL | 유효 종료일 |
| created_at | DATETIME | NOT NULL | 생성일시 |
| updated_at | DATETIME | NOT NULL | 수정일시 |

---

### USER_COUPON (사용자 쿠폰)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| user_coupon_id | BIGINT | PK, AUTO_INCREMENT | 사용자 쿠폰 ID |
| user_id | BIGINT | NOT NULL | 사용자 ID |
| coupon_id | BIGINT | NOT NULL | 쿠폰 ID |
| order_id | BIGINT | NULL | 사용한 주문 ID |
| is_used | BOOLEAN | NOT NULL, DEFAULT FALSE | 사용 여부 |
| issued_at | DATETIME | NOT NULL | 발급일시 |
| used_at | DATETIME | NULL | 사용일시 |
| expired_at | DATETIME | NOT NULL | 만료일시 |
| created_at | DATETIME | NOT NULL | 생성일시 |

**INDEX**: `idx_user_used` (user_id, is_used)

---