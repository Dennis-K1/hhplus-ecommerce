package com.hhplus.ecommerce.domain.exception;

import com.hhplus.ecommerce.common.exception.BusinessException;
import com.hhplus.ecommerce.common.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
