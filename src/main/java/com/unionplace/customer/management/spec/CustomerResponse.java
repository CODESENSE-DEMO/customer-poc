package com.unionplace.customer.management.spec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class CustomerResponse {

    private Long id;
    private String customerNo;

    // [DEFECT-009] 개인정보 보호: 고객명 응답 시 마스킹 처리 누락
    // - 금융사 표준: 이름 가운데 글자 마스킹 (예: 홍길동 → 홍*동)
    // - 운영 환경에서 그대로 노출 시 개인정보보호법 위반 소지
    private String name;

    // [DEFECT-010] 개인정보 유출: 주민등록번호 원문이 응답 DTO에 그대로 포함
    // - 응답 DTO에서는 제외하거나 뒷자리 마스킹(******-1******)이 원칙
    // - 망분리/감사 로그 캡처 시 PII 유출 위험
    private String residentRegistrationNumber;

    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private CustomerStatus status;
    private CustomerTier tier;

    public static CustomerResponse from(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .customerNo(customer.getCustomerNo())
                .name(customer.getName())
                .residentRegistrationNumber(customer.getResidentRegistrationNumber())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .birthDate(customer.getBirthDate())
                .status(customer.getStatus())
                .tier(customer.getTier())
                .build();
    }
}
