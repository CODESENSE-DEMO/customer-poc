package com.unionplace.customer.consent.spec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ConsentResponse {

    private Long id;
    private String customerNo;
    private ConsentType consentType;
    private boolean agreed;
    private LocalDateTime agreedAt;
    private LocalDateTime withdrawnAt;

    public static ConsentResponse from(Consent consent) {
        return ConsentResponse.builder()
                .id(consent.getId())
                .customerNo(consent.getCustomerNo())
                .consentType(consent.getConsentType())
                .agreed(consent.isAgreed())
                .agreedAt(consent.getAgreedAt())
                .withdrawnAt(consent.getWithdrawnAt())
                .build();
    }
}
