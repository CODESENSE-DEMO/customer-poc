package com.unionplace.api.customer.kyc;

import com.unionplace.customer.kyc.spec.KycResponse;
import com.unionplace.customer.kyc.spec.KycService;
import com.unionplace.customer.kyc.spec.KycVerifyRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    @PostMapping("/verify")
    public KycResponse verify(@Valid @RequestBody KycVerifyRequest request) {
        return kycService.verify(request);
    }
}
