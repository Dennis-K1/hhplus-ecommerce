package com.hhplus.ecommerce.domain.exception;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;

public class InsufficientStockException extends BusinessException {

    public InsufficientStockException() {
        super(ErrorCode.INSUFFICIENT_STOCK);
    }
}
