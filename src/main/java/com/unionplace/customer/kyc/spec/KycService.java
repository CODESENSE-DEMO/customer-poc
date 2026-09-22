package com.unionplace.customer.kyc.spec;

public interface KycService {

    KycResponse verify(KycVerifyRequest request);
}
