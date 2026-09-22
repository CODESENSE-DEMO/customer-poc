package com.unionplace.customer.consent.spec;

import java.util.List;

public interface ConsentService {

    ConsentResponse registerConsent(ConsentRequest request);

    List<ConsentResponse> getConsentsOf(String customerNo);

    void withdrawConsent(String customerNo, ConsentType consentType);

    void purgeConsents(String customerNo);

    long countAgreed(ConsentType consentType);

    ConsentSummaryResponse getConsentSummary(String customerNo);
}
