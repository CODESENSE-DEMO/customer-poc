package com.unionplace.customer.consent.spec;

import io.union.core.error.ErrorCode;

/** 동의 도메인 에러 코드. */
public enum ConsentErrorCode implements ErrorCode {

    CONSENT_NOT_FOUND("CONSENT_001", "동의 이력을 찾을 수 없습니다.", 404),
    CONSENT_ALREADY_WITHDRAWN("CONSENT_002", "이미 동의 철회 상태입니다.", 409);

    private final String code;
    private final String message;
    private final int status;

    ConsentErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public int getStatus() { return status; }
}
