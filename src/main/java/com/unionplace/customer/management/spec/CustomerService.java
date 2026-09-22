package com.unionplace.customer.management.spec;

import java.io.IOException;
import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerCreateRequest request);

    CustomerResponse getCustomer(Long id);

    List<CustomerResponse> getAllCustomers();

    CustomerResponse updateContact(Long id, CustomerUpdateRequest request);

    void withdrawCustomer(Long id);

    CustomerResponse findByEmail(String email);

    long countByStatus(CustomerStatus status);

    int importCustomersFromCsv(String filePath) throws IOException;

    List<CustomerResponse> searchCustomers(CustomerSearchRequest request);
}
