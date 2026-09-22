package com.unionplace.api.customer.consent;

import com.unionplace.customer.consent.spec.ConsentRequest;
import com.unionplace.customer.consent.spec.ConsentResponse;
import com.unionplace.customer.consent.spec.ConsentService;
import com.unionplace.customer.consent.spec.ConsentSummaryResponse;
import com.unionplace.customer.consent.spec.ConsentType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consents")
@RequiredArgsConstructor
public class ConsentController {

    private final ConsentService consentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsentResponse register(@Valid @RequestBody ConsentRequest request) {
        return consentService.registerConsent(request);
    }

    @GetMapping("/customers/{customerNo}")
    public List<ConsentResponse> getByCustomer(@PathVariable String customerNo) {
        return consentService.getConsentsOf(customerNo);
    }

    @GetMapping("/customers/{customerNo}/summary")
    public ConsentSummaryResponse getSummary(@PathVariable String customerNo) {
        return consentService.getConsentSummary(customerNo);
    }

    @DeleteMapping("/customers/{customerNo}")
    public void withdraw(
            @PathVariable String customerNo,
            @RequestParam ConsentType type) {
        consentService.withdrawConsent(customerNo, type);
    }

    @GetMapping("/count")
    public Long countAgreed(@RequestParam ConsentType type) {
        return consentService.countAgreed(type);
    }
}
