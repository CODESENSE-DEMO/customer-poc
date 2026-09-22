package com.unionplace.customer.kyc.spec;

import io.union.core.error.ErrorCode;

/** KYC 도메인 에러 코드. */
public enum KycErrorCode implements ErrorCode {

    KYC_VERIFICATION_FAILED("KYC_001", "KYC 인증 처리에 실패했습니다.", 502);

    private final String code;
    private final String message;
    private final int status;

    KycErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public int getStatus() { return status; }
}
