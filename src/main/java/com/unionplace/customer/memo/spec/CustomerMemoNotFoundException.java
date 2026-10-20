package com.unionplace.customer.memo.spec;

import io.union.core.exception.EntityNotFoundException;

public class CustomerMemoNotFoundException extends EntityNotFoundException {
    public CustomerMemoNotFoundException(String message) {
        super(CustomerMemoErrorCode.MEMO_NOT_FOUND, message);
    }
}
