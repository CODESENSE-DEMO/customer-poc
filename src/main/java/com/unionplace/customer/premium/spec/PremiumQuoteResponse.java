package com.unionplace.customer.premium.spec;

import com.unionplace.customer.management.spec.CustomerTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class PremiumQuoteResponse {

    private Long customerId;
    private CustomerTier tier;
    private BigDecimal annualPremium;
    private BigDecimal discountRate;
    private BigDecimal discountAmount;
    private BigDecimal discountedAnnualPremium;
    private int installments;
    private BigDecimal installmentAmount;
}
