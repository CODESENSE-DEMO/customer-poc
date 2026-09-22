package com.unionplace.customer.management.spec;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CustomerCreateRequest {

    @NotBlank(message = "고객번호는 필수입니다.")
    private String customerNo;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "주민등록번호는 필수입니다.")
    @Pattern(regexp = "\\d{6}-\\d{7}", message = "주민등록번호 형식이 올바르지 않습니다.")
    private String residentRegistrationNumber;

    private String email;

    private String phoneNumber;

    @NotNull(message = "생년월일은 필수입니다.")
    private LocalDate birthDate;
}
