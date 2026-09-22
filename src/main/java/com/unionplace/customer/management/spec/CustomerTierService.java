package com.unionplace.customer.management.spec;

import java.util.List;

public interface CustomerTierService {

    CustomerResponse changeTier(Long customerId, TierUpdateRequest request);

    List<CustomerResponse> findByTier(CustomerTier tier);

    long countByTier(CustomerTier tier);
}
