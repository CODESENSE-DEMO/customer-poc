package com.unionplace.customer.management.spec;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomerUpdateRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "휴대전화 번호 형식이 올바르지 않습니다.")
    private String phoneNumber;
}
