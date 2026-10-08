package com.unionplace.customer.premium.spec;

public interface PremiumService {

    PremiumQuoteResponse quote(Long customerId, PremiumQuoteRequest request);
}
