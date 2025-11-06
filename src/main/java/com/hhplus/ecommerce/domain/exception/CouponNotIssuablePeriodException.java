package com.hhplus.ecommerce.domain.exception;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;

public class CouponNotIssuablePeriodException extends BusinessException {

    public CouponNotIssuablePeriodException() {
        super(ErrorCode.COUPON_EXPIRED);  // 기존 ErrorCode 재사용
    }
}
