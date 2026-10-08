package com.unionplace.customer.premium.spec;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class PremiumQuoteRequest {

    @NotNull(message = "연 보험료는 필수입니다.")
    @DecimalMin(value = "0", inclusive = false, message = "연 보험료는 0보다 커야 합니다.")
    private BigDecimal annualPremium;

    @Min(value = 1, message = "납입 회차는 1회 이상이어야 합니다.")
    @Max(value = 12, message = "납입 회차는 12회를 넘을 수 없습니다.")
    private int installments = 1;
}
