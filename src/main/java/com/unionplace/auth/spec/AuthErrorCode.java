package com.unionplace.auth.spec;

import io.union.core.error.ErrorCode;

/** 인증 도메인 에러 코드. */
public enum AuthErrorCode implements ErrorCode {

    INVALID_CREDENTIALS("AUTH_001", "아이디 또는 비밀번호가 올바르지 않습니다.", 401);

    private final String code;
    private final String message;
    private final int status;

    AuthErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public int getStatus() { return status; }
}
