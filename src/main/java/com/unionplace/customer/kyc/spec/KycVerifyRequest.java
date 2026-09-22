package com.unionplace.customer.kyc.spec;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KycVerifyRequest {

    @NotBlank(message = "고객번호는 필수입니다.")
    private String customerNo;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "주민등록번호는 필수입니다.")
    @Pattern(regexp = "\\d{6}-\\d{7}", message = "주민등록번호 형식이 올바르지 않습니다.")
    private String residentRegistrationNumber;
}
