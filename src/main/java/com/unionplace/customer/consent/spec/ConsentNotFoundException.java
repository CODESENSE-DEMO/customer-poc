package com.unionplace.customer.consent.spec;

import io.union.core.exception.EntityNotFoundException;

public class ConsentNotFoundException extends EntityNotFoundException {
    public ConsentNotFoundException(String message) {
        super(ConsentErrorCode.CONSENT_NOT_FOUND, message);
    }
}
