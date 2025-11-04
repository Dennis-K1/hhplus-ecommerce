package com.hhplus.ecommerce.domain.coupon.exception;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;

public class CouponExpiredException extends BusinessException {

    public CouponExpiredException() {
        super(ErrorCode.COUPON_EXPIRED);
    }
}
