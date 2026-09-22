package com.unionplace.customer.management.spec;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TierUpdateRequest {

    @NotNull(message = "등급은 필수입니다.")
    private CustomerTier tier;

    private String reason;
}
