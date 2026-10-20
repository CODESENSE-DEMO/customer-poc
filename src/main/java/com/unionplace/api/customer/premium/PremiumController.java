package com.unionplace.api.customer.premium;

import com.unionplace.customer.premium.spec.PremiumQuoteRequest;
import com.unionplace.customer.premium.spec.PremiumQuoteResponse;
import com.unionplace.customer.premium.spec.PremiumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 보험료 견적 API. 반환값은 프레임워크가 자동으로 ApiResponse 로 감싼다.
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class PremiumController {

    private final PremiumService premiumService;

    @PostMapping("/{id}/premium/quotes")
    public PremiumQuoteResponse quote(
            @PathVariable Long id,
            @Valid @RequestBody PremiumQuoteRequest request) {
        return premiumService.quote(id, request);
    }
}
