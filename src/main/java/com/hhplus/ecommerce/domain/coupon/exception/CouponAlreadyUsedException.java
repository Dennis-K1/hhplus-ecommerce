package com.hhplus.ecommerce.domain.coupon.exception;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;

public class CouponAlreadyUsedException extends BusinessException {

    public CouponAlreadyUsedException() {
        super(ErrorCode.COUPON_ALREADY_USED);
    }
}
