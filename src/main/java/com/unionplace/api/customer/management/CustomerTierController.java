package com.unionplace.api.customer.management;

import com.unionplace.customer.management.spec.CustomerResponse;
import com.unionplace.customer.management.spec.CustomerTier;
import com.unionplace.customer.management.spec.CustomerTierService;
import com.unionplace.customer.management.spec.TierUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerTierController {

    @Autowired
    private CustomerTierService customerTierService;

    @PostMapping("/{id}/changeTier")
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerResponse changeTier(
            @PathVariable Long id,
            @Valid @RequestBody TierUpdateRequest request) {
        return customerTierService.changeTier(id, request);
    }

    @GetMapping("/by-tier")
    public List<CustomerResponse> findByTier(@RequestParam CustomerTier tier) {
        return customerTierService.findByTier(tier);
    }

    @GetMapping("/tier-count")
    public Long countByTier(@RequestParam CustomerTier tier) {
        return customerTierService.countByTier(tier);
    }
}
