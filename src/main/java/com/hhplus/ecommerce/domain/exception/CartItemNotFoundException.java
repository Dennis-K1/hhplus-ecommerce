package com.hhplus.ecommerce.domain.exception;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;

public class CartItemNotFoundException extends BusinessException {

    public CartItemNotFoundException() {
        super(ErrorCode.CART_ITEM_NOT_FOUND);
    }
}
