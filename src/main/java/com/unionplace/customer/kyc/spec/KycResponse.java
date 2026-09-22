package com.unionplace.customer.kyc.spec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class KycResponse {

    private String customerNo;
    private boolean verified;
    private String verificationCode;
    private String message;
}
