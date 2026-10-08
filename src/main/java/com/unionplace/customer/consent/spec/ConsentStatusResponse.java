package com.unionplace.customer.consent.spec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/** 동의 항목별 고객 현황 (마케팅 발송 대상 추출 등에 사용) */
@Getter
@Builder
@AllArgsConstructor
public class ConsentStatusResponse {

    private String customerNo;
    private String customerName;
    private ConsentType consentType;
    private ConsentSummaryResponse.Status status;
    private LocalDateTime agreedAt;
}
