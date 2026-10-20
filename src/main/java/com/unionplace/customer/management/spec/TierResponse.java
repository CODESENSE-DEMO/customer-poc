package com.unionplace.customer.management.spec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TierResponse {

    private Long customerId;
    private CustomerTier tier;
    private boolean premiumBenefitEligible;

    public static TierResponse of(Long customerId, CustomerTier tier) {
        return TierResponse.builder()
                .customerId(customerId)
                .tier(tier)
                .premiumBenefitEligible(tier == CustomerTier.PREMIUM || tier == CustomerTier.VIP)
                .build();
    }
}
