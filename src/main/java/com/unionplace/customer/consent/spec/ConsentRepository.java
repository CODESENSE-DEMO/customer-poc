package com.unionplace.customer.consent.spec;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {

    List<Consent> findByCustomerNo(String customerNo);

    Optional<Consent> findByCustomerNoAndConsentType(String customerNo, ConsentType consentType);

    long countByConsentTypeAndAgreed(ConsentType consentType, boolean agreed);
}
