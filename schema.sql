-- ========================================
-- 이커머스 서비스 DDL
-- ========================================

-- ========================================
-- 1. USER (사용자)
-- ========================================
CREATE TABLE `user` (
    `user_id`      BIGINT          NOT NULL AUTO_INCREMENT COMMENT '사용자 ID',
    `point`        DECIMAL(15, 2)  NOT NULL DEFAULT 0 COMMENT '포인트 잔액',
    `created_at`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자';

-- ========================================
-- 2. PRODUCT (상품)
-- ========================================
CREATE TABLE `product` (
    `product_id`      BIGINT          NOT NULL AUTO_INCREMENT COMMENT '상품 ID',
    `product_name`    VARCHAR(200)    NOT NULL COMMENT '상품명',
    `price`           INT             NOT NULL COMMENT '가격',
    `stock_quantity`  INT             NOT NULL DEFAULT 0 COMMENT '재고 수량',
    `created_at`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`product_id`),
    INDEX `idx_product_name` (`product_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품';

-- ========================================
-- 3. CART_ITEM (장바구니 항목)
-- ========================================
CREATE TABLE `cart_item` (
    `cart_item_id`  BIGINT    NOT NULL AUTO_INCREMENT COMMENT '장바구니 항목 ID',
    `user_id`       BIGINT    NOT NULL COMMENT '사용자 ID',
    `product_id`    BIGINT    NOT NULL COMMENT '상품 ID',
    `quantity`      INT       NOT NULL COMMENT '수량',
    `created_at`    DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at`    DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`cart_item_id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='장바구니 항목';

-- ========================================
-- 4. COUPON (쿠폰)
-- ========================================
CREATE TABLE `coupon` (
    `coupon_id`        BIGINT          NOT NULL AUTO_INCREMENT COMMENT '쿠폰 ID',
    `discount_amount`  DECIMAL(15, 2)  NOT NULL COMMENT '할인 금액',
    `issue_quantity`   INT             NOT NULL COMMENT '발급 수량',
    `issued_quantity`  INT             NOT NULL DEFAULT 0 COMMENT '발급된 수량',
    `valid_from`       DATETIME        NOT NULL COMMENT '유효 시작일',
    `valid_to`         DATETIME        NOT NULL COMMENT '유효 종료일',
    `created_at`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='쿠폰';

-- ========================================
-- 5. ORDER (주문)
-- ========================================
CREATE TABLE `order` (
    `order_id`         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '주문 ID',
    `user_id`          BIGINT          NOT NULL COMMENT '사용자 ID',
    `order_status`     VARCHAR(20)     NOT NULL COMMENT '주문 상태 (PENDING, COMPLETED, CANCELLED)',
    `total_amount`     DECIMAL(15, 2)  NOT NULL COMMENT '총 주문 금액',
    `discount_amount`  DECIMAL(15, 2)  NOT NULL DEFAULT 0 COMMENT '할인 금액',
    `used_coupon_id`   BIGINT          NULL COMMENT '사용한 쿠폰 ID',
    `created_at`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`order_id`),
    INDEX `idx_user_created` (`user_id`, `created_at` DESC),
    INDEX `idx_created_status` (`created_at` DESC, `order_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='주문';

-- ========================================
-- 6. ORDER_ITEM (주문 항목)
-- ========================================
CREATE TABLE `order_item` (
    `order_item_id`  BIGINT    NOT NULL AUTO_INCREMENT COMMENT '주문 항목 ID',
    `order_id`       BIGINT    NOT NULL COMMENT '주문 ID',
    `product_id`     BIGINT    NOT NULL COMMENT '상품 ID',
    `quantity`       INT       NOT NULL COMMENT '수량',
    `price`          INT       NOT NULL COMMENT '상품 가격 (주문 시점)',
    `created_at`     DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    PRIMARY KEY (`order_item_id`),
    INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='주문 항목';

-- ========================================
-- 7. USER_COUPON (사용자 쿠폰)
-- ========================================
CREATE TABLE `user_coupon` (
    `user_coupon_id`  BIGINT    NOT NULL AUTO_INCREMENT COMMENT '사용자 쿠폰 ID',
    `user_id`         BIGINT    NOT NULL COMMENT '사용자 ID',
    `coupon_id`       BIGINT    NOT NULL COMMENT '쿠폰 ID',
    `order_id`        BIGINT    NULL COMMENT '사용한 주문 ID',
    `is_used`         BOOLEAN   NOT NULL DEFAULT FALSE COMMENT '사용 여부',
    `issued_at`       DATETIME  NOT NULL COMMENT '발급일시',
    `used_at`         DATETIME  NULL COMMENT '사용일시',
    `expired_at`      DATETIME  NOT NULL COMMENT '만료일시',
    `created_at`      DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    PRIMARY KEY (`user_coupon_id`),
    INDEX `idx_user_used` (`user_id`, `is_used`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 쿠폰';
