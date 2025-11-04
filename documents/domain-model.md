# 도메인 모델 설계

## 개요

클린 아키텍처 원칙에 따라 **프레임워크와 독립적인 순수 POJO** 도메인 모델을 구현했습니다.

### 설계 원칙
- ✅ 핵심 비즈니스 로직을 도메인 모델 내부에 캡슐화
- ✅ 외부 의존성 제거 (JPA, Spring 등 프레임워크 독립)
- ✅ 불변성 보장 (final 필드 사용)
- ✅ 명확한 의도를 가진 메서드명
- ✅ 도메인별 예외 관리

---

## 패키지 구조

```
domain/
├── product/
│   ├── model/
│   │   └── Product.java                    # 상품 도메인 모델
│   └── exception/
│       ├── InsufficientStockException.java # 재고 부족
│       └── OutOfStockException.java        # 재고 소진
├── coupon/
│   ├── model/
│   │   ├── Coupon.java                     # 쿠폰 도메인 모델
│   │   └── UserCoupon.java                 # 사용자 쿠폰 도메인 모델
│   └── exception/
│       ├── CouponSoldOutException.java     # 쿠폰 수량 소진
│       ├── CouponNotIssuablePeriodException.java # 발급 기간 아님
│       ├── CouponAlreadyUsedException.java # 이미 사용된 쿠폰
│       └── CouponExpiredException.java     # 만료된 쿠폰
├── user/
│   ├── model/
│   │   └── User.java                       # 사용자 도메인 모델
│   └── exception/
│       └── InsufficientBalanceException.java # 잔액 부족
└── order/
    ├── model/
    │   ├── Order.java                      # 주문 도메인 모델
    │   └── OrderStatus.java                # 주문 상태 enum
    └── exception/
        └── InvalidOrderStatusException.java # 유효하지 않은 주문 상태
```

---

## 1. Product (상품)

### 핵심 비즈니스 로직
재고 관리 (차감, 복구, 확인)

### 주요 메서드

```java
public class Product {
    private final long id;
    private final String name;
    private final int price;
    private int stockQuantity;

    // 재고 차감
    public void decreaseStock(int quantity)

    // 재고 복구 (주문 취소 시)
    public void increaseStock(int quantity)

    // 재고 확인
    public boolean hasEnoughStock(int quantity)

    // 재고 소진 여부
    public boolean isOutOfStock()
}
```

### 비즈니스 규칙
- 재고 차감 시 수량 확인 → 부족하면 `InsufficientStockException`
- 재고가 0이면 `OutOfStockException`
- 차감/복구 수량은 0보다 커야 함

---

## 2. Coupon (쿠폰)

### 핵심 비즈니스 로직
쿠폰 발급 관리 (수량 제한, 기간 제한)

### 주요 메서드

```java
public class Coupon {
    private final long id;
    private final int discountAmount;
    private final int issueQuantity;
    private int issuedQuantity;
    private final LocalDateTime validFrom;
    private final LocalDateTime validTo;

    // 발급 가능 여부 (수량만)
    public boolean hasIssueQuantity()

    // 발급 가능 여부 (수량 + 기간)
    public boolean canIssue(LocalDateTime now)

    // 쿠폰 발급 처리
    public void issue(LocalDateTime now)

    // 발급 기간 만료 여부
    public boolean isExpired(LocalDateTime now)
}
```

### 비즈니스 규칙
- 발급 시 기간 확인 → 기간 외이면 `CouponNotIssuablePeriodException`
- 발급 시 수량 확인 → 소진되면 `CouponSoldOutException`
- 발급 순서: 기간 체크 → 수량 체크 → 발급

---

## 3. UserCoupon (사용자 쿠폰)

### 핵심 비즈니스 로직
쿠폰 사용 관리 (중복 사용 방지, 만료 확인)

### 주요 메서드

```java
public class UserCoupon {
    private final long id;
    private final long userId;
    private final long couponId;
    private long orderId;
    private boolean isUsed;
    private final LocalDateTime issuedAt;
    private LocalDateTime usedAt;
    private final LocalDateTime expiredAt;

    // 사용 가능 여부
    public boolean canUse(LocalDateTime now)

    // 만료 여부
    public boolean isExpired(LocalDateTime now)

    // 쿠폰 사용 처리
    public void use(long orderId, LocalDateTime now)
}
```

### 비즈니스 규칙
- 사용 시 중복 체크 → 이미 사용했으면 `CouponAlreadyUsedException`
- 사용 시 만료 체크 → 만료되었으면 `CouponExpiredException`
- 사용 가능 = 미사용 + 미만료

---

## 4. User (사용자)

### 핵심 비즈니스 로직
포인트 관리 (충전, 차감)

### 주요 메서드

```java
public class User {
    private final long id;
    private int point;

    // 포인트 충전
    public void chargePoint(int amount)

    // 포인트 차감
    public void deductPoint(int amount)

    // 포인트 잔액 확인
    public boolean hasEnoughPoint(int amount)
}
```

### 비즈니스 규칙
- 포인트는 0 이상 (음수 불가)
- 차감 시 잔액 확인 → 부족하면 `InsufficientBalanceException`
- 충전/차감 금액은 0보다 커야 함

---

## 5. Order (주문)

### 핵심 비즈니스 로직
주문 생성 및 상태 관리

### 주요 메서드

```java
public class Order {
    private final long id;
    private final long userId;
    private OrderStatus orderStatus;  // PENDING, COMPLETED, CANCELLED
    private final int totalAmount;
    private int discountAmount;
    private long usedCouponId;

    // 할인 적용
    public void applyDiscount(int discountAmount, long usedCouponId)

    // 주문 완료 처리
    public void complete()

    // 주문 취소 처리
    public void cancel()

    // 최종 결제 금액 계산
    public int getFinalAmount()

    // 상태 확인
    public boolean isCompleted()
    public boolean isCancelled()
}
```

### 비즈니스 규칙
- 할인 금액은 총 금액을 초과할 수 없음
- 완료된 주문은 취소 불가
- 취소된 주문은 완료 불가
- 상태 변경 시 유효성 검증 → 실패 시 `InvalidOrderStatusException`

---

## 타입 설계 원칙

### 원시 타입 vs 래퍼 타입
모든 숫자/불리언 타입은 **원시 타입**으로 통일

```java
// 생성자 파라미터: 원시 타입
public Product(long id, String name, int price, int stockQuantity)

// 필드: 원시 타입
private final long id;
private final int price;

// 메서드 파라미터/반환: 원시 타입
public void decreaseStock(int quantity)
public int getPrice()
```

**이유:**
- NPE 걱정 없음
- 성능 최적화 (오토박싱 제거)
- 생성 시 유효성 검증으로 null 방지
- 도메인 객체는 항상 유효한 상태 보장

### 금액 타입
한국 원화 기준 → **int 타입** 사용
- 소수점 없음
- Integer 범위 충분 (최대 약 21억 원)

---

## 예외 처리 전략

### 1. 도메인별 예외 분리
각 도메인 패키지 내부에 `exception` 패키지 생성

### 2. ErrorCode 기반
모든 도메인 예외는 `BusinessException`을 상속하고 `ErrorCode`를 사용

```java
public class InsufficientStockException extends BusinessException {
    public InsufficientStockException() {
        super(ErrorCode.INSUFFICIENT_STOCK);
    }
}
```

### 3. 명확한 예외 구분
- 재고 부족 vs 재고 소진
- 쿠폰 수량 소진 vs 발급 기간 아님
- 쿠폰 이미 사용 vs 쿠폰 만료

---

## 검증 규칙

### 생성자 검증
모든 도메인 객체는 생성 시 유효성 검증

```java
private void validateProduct(long id, String name, int price, int stockQuantity) {
    if (id <= 0) throw new InvalidInputException("상품 ID는 0보다 커야 합니다");
    if (name == null || name.trim().isEmpty()) throw new InvalidInputException("상품명은 필수입니다");
    if (price < 0) throw new InvalidInputException("가격은 0 이상이어야 합니다");
    if (stockQuantity < 0) throw new InvalidInputException("재고는 0 이상이어야 합니다");
}
```

### 메서드 검증
비즈니스 로직 실행 전 파라미터 검증

```java
public void decreaseStock(int quantity) {
    if (quantity <= 0) throw new InvalidInputException("차감할 수량은 0보다 커야 합니다");
    if (isOutOfStock()) throw new OutOfStockException();
    if (!hasEnoughStock(quantity)) throw new InsufficientStockException();
    this.stockQuantity -= quantity;
}
```

---

## 불변성 보장

### final 필드
변경되지 않는 속성은 `final`로 선언

```java
private final long id;           // ID는 변경 불가
private final String name;       // 상품명 변경 불가
private final int price;         // 가격 변경 불가
private int stockQuantity;       // 재고는 변경 가능
```

### setter 없음
외부에서 직접 상태 변경 불가, 비즈니스 메서드를 통해서만 변경

```java
// ❌ Bad
product.setStockQuantity(10);

// ✅ Good
product.decreaseStock(5);
product.increaseStock(3);
```

---

## 다음 단계

### Infrastructure 계층
- JPA 엔티티 구현
- 도메인 모델 ↔ JPA 엔티티 Mapper
- Repository 구현

### Application 계층
- UseCase/Service 구현
- 트랜잭션 관리
- 도메인 객체 조합

### 단위 테스트
- 도메인 로직 테스트
- Mock 없이 순수 테스트 가능
