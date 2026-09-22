package com.unionplace.customer.management.spec;

import java.util.List;

public interface CustomerBlockService {

    CustomerResponse blockCustomer(Long customerId, BlockRequest request);

    CustomerResponse unblockCustomer(Long customerId);

    List<CustomerResponse> findBlockedCustomers();
}
