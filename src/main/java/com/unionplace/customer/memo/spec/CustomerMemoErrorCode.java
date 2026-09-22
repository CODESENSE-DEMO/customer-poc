package com.unionplace.customer.memo.spec;

import io.union.core.error.ErrorCode;

/** 상담 메모 도메인 에러 코드. */
public enum CustomerMemoErrorCode implements ErrorCode {

    MEMO_NOT_FOUND("MEMO_001", "상담 메모를 찾을 수 없습니다.", 404),
    MEMO_ALREADY_DELETED("MEMO_002", "이미 삭제된 상담 메모입니다.", 409),
    MEMO_NOT_OWNED("MEMO_003", "다른 고객의 상담 메모입니다.", 403);

    private final String code;
    private final String message;
    private final int status;

    CustomerMemoErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public int getStatus() { return status; }
}
