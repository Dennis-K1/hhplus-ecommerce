package com.hhplus.ecommerce.domain.exception;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;

public class CouponNotFoundException extends BusinessException {

    public CouponNotFoundException() {
        super(ErrorCode.COUPON_NOT_FOUND);
    }
}
