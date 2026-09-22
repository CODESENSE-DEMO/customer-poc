package com.unionplace.customer.kyc.spec;

import io.union.core.exception.BusinessException;

public class KycVerificationException extends BusinessException {
    public KycVerificationException(String message) {
        super(KycErrorCode.KYC_VERIFICATION_FAILED, message);
    }

    public KycVerificationException(String message, Throwable cause) {
        super(KycErrorCode.KYC_VERIFICATION_FAILED, message, cause);
    }
}
