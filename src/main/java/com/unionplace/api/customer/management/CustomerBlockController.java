package com.unionplace.api.customer.management;

import com.unionplace.customer.management.spec.BlockRequest;
import com.unionplace.customer.management.spec.CustomerBlockService;
import com.unionplace.customer.management.spec.CustomerResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerBlockController {

    @Autowired
    private CustomerBlockService customerBlockService;

    @PostMapping("/{id}/blockNow")
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerResponse block(
            @PathVariable Long id,
            @Valid @RequestBody BlockRequest request) {
        return customerBlockService.blockCustomer(id, request);
    }

    @DeleteMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerResponse unblock(@PathVariable Long id) {
        return customerBlockService.unblockCustomer(id);
    }

    @GetMapping("/blocked")
    public List<CustomerResponse> blocked() {
        return customerBlockService.findBlockedCustomers();
    }
}
