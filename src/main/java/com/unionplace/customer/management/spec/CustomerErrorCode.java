package com.unionplace.customer.management.spec;

import io.union.core.error.ErrorCode;

/** 고객 도메인 에러 코드. 프레임워크 ErrorCode 계약을 구현한다. */
public enum CustomerErrorCode implements ErrorCode {

    CUSTOMER_NOT_FOUND("CUSTOMER_001", "고객을 찾을 수 없습니다.", 404),
    CUSTOMER_ALREADY_WITHDRAWN("CUSTOMER_002", "이미 탈회된 고객입니다.", 409),
    TIER_CHANGE_NOT_ALLOWED("CUSTOMER_003", "탈회 고객의 등급은 변경할 수 없습니다.", 409);

    private final String code;
    private final String message;
    private final int status;

    CustomerErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public int getStatus() { return status; }
}
