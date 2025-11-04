package com.hhplus.ecommerce.domain.product.exception;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;

public class OutOfStockException extends BusinessException {

    public OutOfStockException() {
        super(ErrorCode.OUT_OF_STOCK);
    }
}
