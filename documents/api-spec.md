# 이커머스 API 명세

## Common Response

### Success Response
```typescript
{
  success: boolean
  data: object
  message?: string
}
```

### Error Response
```typescript
{
  success: boolean
  error: {
    code: string
    message: string
  }
}
```

---

## Common Error Codes

### 4xx Client Errors

#### 400 Bad Request (요청 형식 오류)
| Error Code | 설명 |
|-----------|------|
| `INVALID_INPUT` | 잘못된 입력 값 (필수 파라미터 누락, 타입 불일치, JSON 파싱 실패 등) |

#### 404 Not Found (리소스 없음)
| Error Code | 설명 |
|-----------|------|
| `USER_NOT_FOUND` | 사용자를 찾을 수 없음 |
| `PRODUCT_NOT_FOUND` | 상품을 찾을 수 없음 |
| `ORDER_NOT_FOUND` | 주문을 찾을 수 없음 |
| `CART_ITEM_NOT_FOUND` | 장바구니 항목을 찾을 수 없음 |
| `COUPON_NOT_FOUND` | 쿠폰을 찾을 수 없음 |
| `USER_COUPON_NOT_FOUND` | 사용자 쿠폰을 찾을 수 없음 |

#### 409 Conflict (중복/충돌)
| Error Code | 설명 |
|-----------|------|
| `COUPON_ALREADY_ISSUED` | 이미 발급받은 쿠폰 |
| `ORDER_ALREADY_COMPLETED` | 이미 완료된 주문 |
| `ORDER_ALREADY_CANCELLED` | 이미 취소된 주문 |

#### 422 Unprocessable Entity (비즈니스 로직 검증 실패)
| Error Code | 설명 |
|-----------|------|
| `CART_EMPTY` | 장바구니가 비어있음 |
| `INSUFFICIENT_STOCK` | 재고 부족 |
| `OUT_OF_STOCK` | 재고 없음 |
| `INVALID_ORDER_STATUS` | 유효하지 않은 주문 상태 (결제 불가능한 상태) |
| `INSUFFICIENT_BALANCE` | 잔액 부족 |
| `COUPON_SOLD_OUT` | 쿠폰 수량 소진 |
| `COUPON_ALREADY_USED` | 이미 사용된 쿠폰 |
| `COUPON_EXPIRED` | 만료된 쿠폰 |
| `COUPON_NOT_OWNED` | 소유하지 않은 쿠폰 |

### 5xx Server Errors

#### 500 Internal Server Error
| Error Code | 설명 |
|-----------|------|
| `INTERNAL_SERVER_ERROR` | 서버 내부 오류 |

### Error Response Example

```json
{
  "success": false,
  "error": {
    "code": "INSUFFICIENT_STOCK",
    "message": "상품의 재고가 부족합니다. 요청 수량: 10, 현재 재고: 5"
  }
}
```

---

## 1. 상품 API잘ㄱㄴ메ㄲㄸㅊ
**Endpoint**: `GET /api/products`

**Query Parameters**:
```typescript
{
  page?: number        // default: 1
  size?: number        // default: 20
  search?: string      // 상품명 검색어
}
```

**Request Example**:
```
GET /api/products?page=1&size=20&search=노트북
```

**Response (200 OK)**:
```typescript
{
  success: boolean
  data: {
    products: Array<{
      productId: number
      productName: string
      price: number
      stockQuantity: number
      createdAt: string      // ISO 8601 format
      updatedAt: string      // ISO 8601 format
    }>
    pagination: {
      currentPage: number
      pageSize: number
      totalItems: number
      totalPages: number
    }
  }
}
```

**Response Example**:
```json
{
  "success": true,
  "data": {
    "products": [
      {
        "productId": 1,
        "productName": "노트북",
        "price": 1500000,
        "stockQuantity": 50,
        "createdAt": "2025-01-01T00:00:00",
        "updatedAt": "2025-01-01T00:00:00"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "pageSize": 20,
      "totalItems": 100,
      "totalPages": 5
    }
  }
}
```

**Error Responses**:
- `INVALID_INPUT` (400): 잘못된 페이지 번호 또는 페이지 크기

---

### 1.2 상품 상세 조회

**Endpoint**: `GET /api/products/{productId}`

**Path Parameters**:
```typescript
{
  productId: number
}
```

**Request Example**:
```
GET /api/products/1
```

**Response (200 OK)**:
```typescript
{
  success: boolean
  data: {
    productId: number
    productName: string
    price: number
    stockQuantity: number
    createdAt: string      // ISO 8601 format
    updatedAt: string      // ISO 8601 format
  }
}
```

**Response Example**:
```json
{
  "success": true,
  "data": {
    "productId": 1,
    "productName": "노트북",
    "price": 1500000,
    "stockQuantity": 50,
    "createdAt": "2025-01-01T00:00:00",
    "updatedAt": "2025-01-01T00:00:00"
  }
}
```

**Error Responses**:
- `PRODUCT_NOT_FOUND` (404): 상품을 찾을 수 없음

---

### 1.3 인기 상품 조회

**Endpoint**: `GET /api/products/popular`

**Request Example**:
```
GET /api/products/popular
```

**Response (200 OK)**:
```typescript
{
  success: boolean
  data: {
    products: Array<{
      productId: number
      productName: string
      price: number
      stockQuantity: number
      salesCount: number   // 최근 3일간 판매량
    }>
  }
  message: string
}
```

**Response Example**:
```json
{
  "success": true,
  "data": {
    "products": [
      {
        "productId": 1,
        "productName": "노트북",
        "price": 1500000,
        "stockQuantity": 50,
        "salesCount": 150
      },
      {
        "productId": 2,
        "productName": "마우스",
        "price": 50000,
        "stockQuantity": 200,
        "salesCount": 120
      }
    ]
  },
  "message": "최근 3일간 판매량 기준 Top 5"
}
```

---

## 2. 장바구니 API

### 2.1 장바구니 조회

**Endpoint**: `GET /api/users/{userId}/carts`

**Path Parameters**:
```typescript
{
  userId: number
}
```

**Request Example**:
```
GET /api/users/1/carts
```

**Response (200 OK)**:
```typescript
{
  success: boolean
  data: {
    items: Array<{
      cartItemId: number
      product: {
        productId: number
        productName: string
        price: number
      }
      quantity: number
      amount: number       // price * quantity
    }>
    totalAmount: number
  }
}
```

**Response Example**:
```json
{
  "success": true,
  "data": {
    "items": [
      {
        "cartItemId": 1,
        "product": {
          "productId": 10,
          "productName": "노트북",
          "price": 1500000
        },
        "quantity": 2,
        "amount": 3000000
      }
    ],
    "totalAmount": 3000000
  }
}
```

**Error Responses**:
- `USER_NOT_FOUND` (404): 사용자를 찾을 수 없음

---

### 2.2 장바구니 추가

**Endpoint**: `POST /api/users/{userId}/carts`

**Path Parameters**:
```typescript
{
  userId: number
}
```

**Request Body**:
```typescript
{
  productId: number
  quantity: number
}
```

**Request Example**:
```json
{
  "productId": 10,
  "quantity": 2
}
```

**Response (201 Created)**:
```typescript
{
  success: boolean
  data: {
    cartItemId: number
    userId: number
    productId: number
    quantity: number
    createdAt: string    // ISO 8601 format
  }
  message: string
}
```

**Response Example**:
```json
{
  "success": true,
  "data": {
    "cartItemId": 1,
    "userId": 1,
    "productId": 10,
    "quantity": 2,
    "createdAt": "2025-01-01T00:00:00"
  },
  "message": "장바구니에 상품이 추가되었습니다"
}
```

**Error Responses**:
- `INVALID_INPUT` (400): 수량이 0 이하
- `PRODUCT_NOT_FOUND` (404): 상품을 찾을 수 없음
- `USER_NOT_FOUND` (404): 사용자를 찾을 수 없음
- `INSUFFICIENT_STOCK` (422): 재고 부족

---

### 2.3 장바구니 수량 수정

**Endpoint**: `PATCH /api/users/{userId}/carts/{cartItemId}`

**Path Parameters**:
```typescript
{
  userId: number
  cartItemId: number
}
```

**Request Body**:
```typescript
{
  quantity: number
}
```

**Request Example**:
```json
{
  "quantity": 5
}
```

**Response (200 OK)**:
```typescript
{
  success: boolean
  data: {
    cartItemId: number
    userId: number
    productId: number
    quantity: number
    updatedAt: string    // ISO 8601 format
  }
  message: string
}
```

**Response Example**:
```json
{
  "success": true,
  "data": {
    "cartItemId": 1,
    "userId": 1,
    "productId": 10,
    "quantity": 5,
    "updatedAt": "2025-01-01T01:00:00"
  },
  "message": "장바구니 수량이 수정되었습니다"
}
```

**Error Responses**:
- `INVALID_INPUT` (400): 수량이 0 이하
- `CART_ITEM_NOT_FOUND` (404): 장바구니 항목을 찾을 수 없음
- `INSUFFICIENT_STOCK` (422): 재고 부족

---

### 2.4 장바구니 삭제

**Endpoint**: `DELETE /api/users/{userId}/carts/{cartItemId}`

**Path Parameters**:
```typescript
{
  userId: number
  cartItemId: number
}
```

**Request Example**:
```
DELETE /api/users/1/carts/1
```

**Response (200 OK)**:
```typescript
{
  success: boolean
  message: string
}
```

**Response Example**:
```json
{
  "success": true,
  "message": "장바구니에서 상품이 제거되었습니다"
}
```

**Error Responses**:
- `CART_ITEM_NOT_FOUND` (404): 장바구니 항목을 찾을 수 없음

---

## 3. 주문 API

### 3.1 주문 생성

**Endpoint**: `POST /api/orders`

**Request Body**:
```typescript
{
  userId: number
  orderType: "CART" | "DIRECT"
  productId?: number       // orderType이 DIRECT일 경우 필수
  quantity?: number        // orderType이 DIRECT일 경우 필수
}
```

**Request Example (장바구니 주문)**:
```json
{
  "userId": 1,
  "orderType": "CART"
}
```

**Request Example (즉시 구매)**:
```json
{
  "userId": 1,
  "orderType": "DIRECT",
  "productId": 10,
  "quantity": 1
}
```

**Response (201 Created)**:
```typescript
{
  success: boolean
  data: {
    orderId: number
    userId: number
    orderStatus: "PENDING" | "COMPLETED" | "CANCELLED"
    items: Array<{
      orderItemId: number
      productId: number
      productName: string
      quantity: number
      price: number
    }>
    totalAmount: number
    discountAmount: number
    createdAt: string      // ISO 8601 format
  }
  message: string
}
```

**Response Example**:
```json
{
  "success": true,
  "data": {
    "orderId": 100,
    "userId": 1,
    "orderStatus": "PENDING",
    "items": [
      {
        "orderItemId": 1,
        "productId": 10,
        "productName": "노트북",
        "quantity": 2,
        "price": 1500000
      }
    ],
    "totalAmount": 3000000,
    "discountAmount": 0,
    "createdAt": "2025-01-01T00:00:00"
  },
  "message": "주문이 생성되었습니다"
}
```

**Error Responses**:
- `INVALID_INPUT` (400): orderType이 DIRECT인데 productId/quantity 누락, 수량이 0 이하
- `USER_NOT_FOUND` (404): 사용자를 찾을 수 없음
- `PRODUCT_NOT_FOUND` (404): 상품을 찾을 수 없음 (DIRECT 주문 시)
- `CART_EMPTY` (422): 장바구니가 비어있음 (CART 주문 시)
- `INSUFFICIENT_STOCK` (422): 재고 부족
- `OUT_OF_STOCK` (422): 재고 없음

---

### 3.2 주문 상세 조회

**Endpoint**: `GET /api/orders/{orderId}`

**Path Parameters**:
```typescript
{
  orderId: number
}
```

**Request Example**:
```
GET /api/orders/100
```

**Response (200 OK)**:
```typescript
{
  success: boolean
  data: {
    orderId: number
    userId: number
    orderStatus: "PENDING" | "COMPLETED" | "CANCELLED"
    items: Array<{
      orderItemId: number
      productId: number
      productName: string
      quantity: number
      price: number
    }>
    totalAmount: number
    discountAmount: number
    usedCouponId: number | null
    finalAmount: number
    createdAt: string      // ISO 8601 format
    updatedAt: string      // ISO 8601 format
  }
}
```

**Response Example**:
```json
{
  "success": true,
  "data": {
    "orderId": 100,
    "userId": 1,
    "orderStatus": "COMPLETED",
    "items": [
      {
        "orderItemId": 1,
        "productId": 10,
        "productName": "노트북",
        "quantity": 2,
        "price": 1500000
      }
    ],
    "totalAmount": 3000000,
    "discountAmount": 500000,
    "usedCouponId": 5,
    "finalAmount": 2500000,
    "createdAt": "2025-01-01T00:00:00",
    "updatedAt": "2025-01-01T00:05:00"
  }
}
```

**Error Responses**:
- `ORDER_NOT_FOUND` (404): 주문을 찾을 수 없음

---

### 3.3 주문 목록 조회

**Endpoint**: `GET /api/orders`

**Query Parameters**:
```typescript
{
  userId: number
  page?: number        // default: 1
  size?: number        // default: 20
}
```

**Request Example**:
```
GET /api/orders?userId=1&page=1&size=20
```

**Response (200 OK)**:
```typescript
{
  success: boolean
  data: {
    orders: Array<{
      orderId: number
      orderStatus: "PENDING" | "COMPLETED" | "CANCELLED"
      totalAmount: number
      discountAmount: number
      finalAmount: number
      createdAt: string      // ISO 8601 format
    }>
    pagination: {
      currentPage: number
      pageSize: number
      totalItems: number
      totalPages: number
    }
  }
}
```

**Response Example**:
```json
{
  "success": true,
  "data": {
    "orders": [
      {
        "orderId": 100,
        "orderStatus": "COMPLETED",
        "totalAmount": 3000000,
        "discountAmount": 500000,
        "finalAmount": 2500000,
        "createdAt": "2025-01-01T00:00:00"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "pageSize": 20,
      "totalItems": 10,
      "totalPages": 1
    }
  }
}
```

**Error Responses**:
- `INVALID_INPUT` (400): 사용자 ID 누락 또는 잘못된 페이지 정보
- `USER_NOT_FOUND` (404): 사용자를 찾을 수 없음

---

## 4. 결제 API

### 4.1 잔액 조회

**Endpoint**: `GET /api/users/{userId}/balance`

**Path Parameters**:
```typescript
{
  userId: number
}
```

**Request Example**:
```
GET /api/users/1/balance
```

**Response (200 OK)**:
```typescript
{
  success: boolean
  data: {
    userId: number
    balance: number
  }
}
```

**Response Example**:
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "balance": 1000000
  }
}
```

**Error Responses**:
- `USER_NOT_FOUND` (404): 사용자를 찾을 수 없음

---

### 4.2 잔액 충전

**Endpoint**: `POST /api/users/{userId}/balance`

**Path Parameters**:
```typescript
{
  userId: number
}
```

**Request Body**:
```typescript
{
  amount: number
}
```

**Request Example**:
```json
{
  "amount": 500000
}
```

**Response (200 OK)**:
```typescript
{
  success: boolean
  data: {
    userId: number
    previousBalance: number
    chargeAmount: number
    currentBalance: number
  }
  message: string
}
```

**Response Example**:
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "previousBalance": 1000000,
    "chargeAmount": 500000,
    "currentBalance": 1500000
  },
  "message": "잔액이 충전되었습니다"
}
```

**Error Responses**:
- `INVALID_INPUT` (400): 충전 금액이 0 이하
- `USER_NOT_FOUND` (404): 사용자를 찾을 수 없음

---

### 4.3 결제 실행

**Endpoint**: `POST /api/payments`

**Request Body**:
```typescript
{
  orderId: number
  userId: number
  userCouponId?: number    // optional
}
```

**Request Example**:
```json
{
  "orderId": 100,
  "userId": 1,
  "userCouponId": 5
}
```

**Response (200 OK)**:
```typescript
{
  success: boolean
  data: {
    paymentId: number
    orderId: number
    userId: number
    totalAmount: number
    discountAmount: number
    finalAmount: number
    remainingBalance: number
    orderStatus: "COMPLETED"
    createdAt: string      // ISO 8601 format
  }
  message: string
}
```

**Response Example**:
```json
{
  "success": true,
  "data": {
    "paymentId": 1,
    "orderId": 100,
    "userId": 1,
    "totalAmount": 3000000,
    "discountAmount": 500000,
    "finalAmount": 2500000,
    "remainingBalance": 500000,
    "orderStatus": "COMPLETED",
    "createdAt": "2025-01-01T00:05:00"
  },
  "message": "결제가 완료되었습니다"
}
```

**Error Responses**:
- `USER_NOT_FOUND` (404): 사용자를 찾을 수 없음
- `ORDER_NOT_FOUND` (404): 주문을 찾을 수 없음
- `USER_COUPON_NOT_FOUND` (404): 사용자 쿠폰을 찾을 수 없음
- `ORDER_ALREADY_COMPLETED` (409): 이미 완료된 주문
- `ORDER_ALREADY_CANCELLED` (409): 이미 취소된 주문
- `INVALID_ORDER_STATUS` (422): 주문 상태가 PENDING이 아님
- `INSUFFICIENT_BALANCE` (422): 잔액 부족
- `COUPON_ALREADY_USED` (422): 이미 사용된 쿠폰
- `COUPON_EXPIRED` (422): 만료된 쿠폰
- `COUPON_NOT_OWNED` (422): 소유하지 않은 쿠폰

---

## 5. 쿠폰 API

### 5.1 사용자 쿠폰 조회

**Endpoint**: `GET /api/users/{userId}/coupons`

**Path Parameters**:
```typescript
{
  userId: number
}
```

**Request Example**:
```
GET /api/users/1/coupons
```

**Response (200 OK)**:
```typescript
{
  success: boolean
  data: {
    coupons: Array<{
      userCouponId: number
      coupon: {
        couponId: number
        couponName: string
        discountAmount: number
      }
      isUsed: boolean
      issuedAt: string       // ISO 8601 format
      expiredAt: string      // ISO 8601 format
    }>
  }
}
```

**Response Example**:
```json
{
  "success": true,
  "data": {
    "coupons": [
      {
        "userCouponId": 1,
        "coupon": {
          "couponId": 5,
          "couponName": "신규가입 할인쿠폰",
          "discountAmount": 10000
        },
        "isUsed": false,
        "issuedAt": "2025-01-01T00:00:00",
        "expiredAt": "2025-12-31T23:59:59"
      }
    ]
  }
}
```

**Error Responses**:
- `USER_NOT_FOUND` (404): 사용자를 찾을 수 없음

---

### 5.2 쿠폰 발급

**Endpoint**: `POST /api/users/{userId}/coupons`

**Path Parameters**:
```typescript
{
  userId: number
}
```

**Request Body**:
```typescript
{
  couponId: number
}
```

**Request Example**:
```json
{
  "couponId": 5
}
```

**Response (201 Created)**:
```typescript
{
  success: boolean
  data: {
    userCouponId: number
    userId: number
    coupon: {
      couponId: number
      couponName: string
      discountAmount: number
    }
    isUsed: boolean
    issuedAt: string       // ISO 8601 format
    expiredAt: string      // ISO 8601 format
  }
  message: string
}
```

**Response Example**:
```json
{
  "success": true,
  "data": {
    "userCouponId": 1,
    "userId": 1,
    "coupon": {
      "couponId": 5,
      "couponName": "신규가입 할인쿠폰",
      "discountAmount": 10000
    },
    "isUsed": false,
    "issuedAt": "2025-01-01T00:00:00",
    "expiredAt": "2025-12-31T23:59:59"
  },
  "message": "쿠폰이 발급되었습니다"
}
```

**Error Responses**:
- `USER_NOT_FOUND` (404): 사용자를 찾을 수 없음
- `COUPON_NOT_FOUND` (404): 쿠폰을 찾을 수 없음
- `COUPON_ALREADY_ISSUED` (409): 이미 발급받은 쿠폰
- `COUPON_SOLD_OUT` (422): 쿠폰 수량 소진

---

## 6. 데이터 플랫폼 전송 (내부 인터페이스)

### 6.1 주문 데이터 전송

결제 완료 시 외부 시스템으로 주문 데이터 전송

**구현 방식**:
- 실제 API 호출은 구현하지 않고 로그 출력
- 예: `log.info("외부 시스템 전송: 주문ID={}, 금액={}", orderId, amount)`
- 전송 실패해도 결제는 정상 완료
- 트랜잭션과 분리하여 비동기 처리

**전송 데이터 형식**:
```typescript
{
  orderId: number
  userId: number
  totalAmount: number
  discountAmount: number
  finalAmount: number
  items: Array<{
    productId: number
    productName: string
    quantity: number
    price: number
  }>
  completedAt: string      // ISO 8601 format
}
```

**전송 데이터 예시**:
```json
{
  "orderId": 100,
  "userId": 1,
  "totalAmount": 3000000,
  "discountAmount": 500000,
  "finalAmount": 2500000,
  "items": [
    {
      "productId": 10,
      "productName": "노트북",
      "quantity": 2,
      "price": 1500000
    }
  ],
  "completedAt": "2025-01-01T00:05:00"
}
```
