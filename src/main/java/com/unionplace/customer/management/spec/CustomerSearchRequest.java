package com.unionplace.customer.management.spec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSearchRequest {

    private String nameKeyword;
    private CustomerStatus status;
    private LocalDate birthDateFrom;
    private LocalDate birthDateTo;

    public boolean hasNameKeyword() {
        return nameKeyword != null && !nameKeyword.isBlank();
    }

    public boolean hasStatus() {
        return status != null;
    }

    public boolean hasBirthDateRange() {
        return birthDateFrom != null && birthDateTo != null;
    }
}
