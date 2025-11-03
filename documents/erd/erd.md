# 이커머스 서비스 ERD

## 도메인별 엔티티 관계도

### 1. 사용자 도메인 (User Domain)
```
┌─────────────────────────────────────┐
│         사용자 도메인               │
│                                     │
│  ┌──────────┐                       │
│  │   USER   │                       │
│  │  (사용자) │                       │
│  └──────────┘                       │
│       │                             │
│       │ point (포인트 잔액)         │
│       │ created_at, updated_at      │
└───────┼─────────────────────────────┘
        │
        │ 1:N 관계 (다른 도메인과 연결)
        ├─→ CART_ITEM (장바구니)
        ├─→ ORDER (주문)
        └─→ USER_COUPON (쿠폰 발급 이력)
```

### 2. 상품 도메인 (Product Domain)
```
┌─────────────────────────────────────┐
│         상품 도메인                 │
│                                     │
│  ┌──────────┐                       │
│  │ PRODUCT  │                       │
│  │  (상품)   │                       │
│  └──────────┘                       │
│       │                             │
│       │ product_name (상품명)       │
│       │ price (가격)                │
│       │ stock_quantity (재고)       │
│       │ created_at, updated_at      │
└───────┼─────────────────────────────┘
        │
        │ 1:N 관계 (다른 도메인과 연결)
        ├─→ CART_ITEM (장바구니 항목)
        └─→ ORDER_ITEM (주문 항목)
```

### 3. 장바구니 도메인 (Cart Domain)
```
┌─────────────────────────────────────┐
│        장바구니 도메인              │
│                                     │
│  ┌──────────────┐                  │
│  │  CART_ITEM   │                  │
│  │ (장바구니 항목)│                  │
│  └──────────────┘                  │
│       │                             │
│       │ user_id (FK → USER)        │
│       │ product_id (FK → PRODUCT)  │
│       │ quantity (수량)             │
│       │ created_at, updated_at      │
│       │                             │
│       │ UNIQUE(user_id, product_id) │
└───────┼─────────────────────────────┘
        │
        │ 연결 정보
        ├─→ USER (N:1)
        └─→ PRODUCT (N:1)
```

### 4. 주문 도메인 (Order Domain)
```
┌─────────────────────────────────────────────────┐
│              주문 도메인                        │
│                                                 │
│  ┌──────────┐          ┌──────────────┐        │
│  │  ORDER   │ 1:N      │  ORDER_ITEM  │        │
│  │  (주문)   │─────────▶│  (주문 항목)  │        │
│  └──────────┘          └──────────────┘        │
│       │                      │                  │
│       │ user_id (FK)         │ order_id (FK)    │
│       │ order_status         │ product_id (FK)  │
│       │ total_amount         │ quantity         │
│       │ discount_amount      │ price            │
│       │ used_coupon_id       │                  │
│       │ created_at           │ created_at       │
└───────┼──────────────────────┼──────────────────┘
        │                      │
        │ 연결 정보            │
        ├─→ USER (N:1)        └─→ PRODUCT (N:1)
        └─→ USER_COUPON (N:1)
```

### 5. 쿠폰 도메인 (Coupon Domain)
```
┌─────────────────────────────────────────────────┐
│              쿠폰 도메인                        │
│                                                 │
│  ┌──────────┐          ┌──────────────┐        │
│  │  COUPON  │ 1:N      │ USER_COUPON  │        │
│  │  (쿠폰)   │─────────▶│ (사용자 쿠폰) │        │
│  └──────────┘          └──────────────┘        │
│       │                      │                  │
│       │ discount_amount      │ user_id (FK)     │
│       │ issue_quantity       │ coupon_id (FK)   │
│       │ issued_quantity      │ order_id (FK)    │
│       │ valid_from           │ is_used          │
│       │ valid_to             │ issued_at        │
│       │ created_at           │ used_at          │
│       │ updated_at           │ expired_at       │
└───────┼──────────────────────┼──────────────────┘
        │                      │
        │                      │ 연결 정보
        │                      ├─→ USER (N:1)
        │                      ├─→ COUPON (N:1)
        │                      └─→ ORDER (N:1)
        └─────────────────────────────────────────
```

### 전체 도메인 관계 요약
```
┌─────────┐
│  USER   │
│ (사용자) │
└────┬────┘
     │
     ├─────────────┐
     │             │
     ▼             ▼
┌─────────┐   ┌──────────┐
│  CART   │   │  ORDER   │
│(장바구니)│   │  (주문)   │
└─────────┘   └──────────┘
     │             │
     │             ▼
     │        ┌──────────────┐
     │        │  ORDER_ITEM  │
     │        │  (주문 항목)  │
     │        └──────────────┘
     │             │
     ▼             ▼
┌──────────┐  ┌──────────┐
│ PRODUCT  │  │  COUPON  │
│  (상품)   │  │  (쿠폰)   │
└──────────┘  └──────────┘
                    │
                    ▼
              ┌─────────────┐
              │ USER_COUPON │
              │(사용자 쿠폰) │
              └─────────────┘
```

---

## 테이블 상세

### USER (사용자)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| user_id | BIGINT | PK, AUTO_INCREMENT | 사용자 ID |
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
| price | INT | NOT NULL | 상품 가격 (주문 시점) |
| created_at | DATETIME | NOT NULL | 생성일시 |

**INDEX**: `idx_order_id` (order_id)

---

### COUPON (쿠폰)
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| coupon_id | BIGINT | PK, AUTO_INCREMENT | 쿠폰 ID |
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