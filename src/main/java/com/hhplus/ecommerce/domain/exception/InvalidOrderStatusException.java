package com.hhplus.ecommerce.domain.exception;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;

public class InvalidOrderStatusException extends BusinessException {

    public InvalidOrderStatusException() {
        super(ErrorCode.INVALID_ORDER_STATUS);
    }
}
