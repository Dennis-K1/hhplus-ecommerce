package com.hhplus.ecommerce.domain.exception;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;

public class CouponSoldOutException extends BusinessException {

    public CouponSoldOutException() {
        super(ErrorCode.COUPON_SOLD_OUT);
    }
}
