package com.unionplace.customer.consent.spec;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConsentRequest {

    @NotBlank(message = "고객번호는 필수입니다.")
    private String customerNo;

    @NotNull(message = "동의 항목은 필수입니다.")
    private ConsentType consentType;

    private boolean agreed;
}
