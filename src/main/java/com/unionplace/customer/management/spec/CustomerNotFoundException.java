package com.unionplace.customer.management.spec;

import io.union.core.exception.EntityNotFoundException;

public class CustomerNotFoundException extends EntityNotFoundException {
    public CustomerNotFoundException(String message) {
        super(CustomerErrorCode.CUSTOMER_NOT_FOUND, message);
    }
}
