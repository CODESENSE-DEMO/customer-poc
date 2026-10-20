package com.unionplace.customer.premium.impl;

import com.unionplace.customer.management.spec.Customer;
import com.unionplace.customer.management.spec.CustomerNotFoundException;
import com.unionplace.customer.management.spec.CustomerRepository;
import com.unionplace.customer.management.spec.CustomerTier;
import com.unionplace.customer.premium.spec.PremiumQuoteRequest;
import com.unionplace.customer.premium.spec.PremiumQuoteResponse;
import com.unionplace.customer.premium.spec.PremiumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;

/**
 * 고객 등급별 보험료 할인 및 분납 금액 산출.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {

    private final CustomerRepository customerRepository;

    /** 등급별 할인율 (연 보험료 기준) */
    private static final Map<CustomerTier, BigDecimal> DISCOUNT_RATES = new EnumMap<>(CustomerTier.class);

    static {
        DISCOUNT_RATES.put(CustomerTier.BASIC, BigDecimal.ZERO);
        DISCOUNT_RATES.put(CustomerTier.STANDARD, new BigDecimal("0.02"));
        DISCOUNT_RATES.put(CustomerTier.PREMIUM, new BigDecimal("0.05"));
        DISCOUNT_RATES.put(CustomerTier.VIP, new BigDecimal("0.10"));
    }

    @Override
    @Transactional(readOnly = true)
    public PremiumQuoteResponse quote(Long customerId, PremiumQuoteRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("고객을 찾을 수 없습니다. id=" + customerId));

        BigDecimal annualPremium = request.getAnnualPremium();
        BigDecimal discountRate = DISCOUNT_RATES.getOrDefault(customer.getTier(), BigDecimal.ZERO);

        // 할인액은 원 단위 반올림
        BigDecimal discountAmount = annualPremium.multiply(discountRate).setScale(0, RoundingMode.HALF_UP);
        BigDecimal discountedAnnual = annualPremium.subtract(discountAmount);

        // 분납 금액 = 할인 적용 연 보험료 / 납입 회차
        BigDecimal installmentAmount = discountedAnnual.divide(BigDecimal.valueOf(request.getInstallments()));

        log.info("보험료 산출: customerId={}, tier={}, annual={}, discounted={}, installments={}",
                customerId, customer.getTier(), annualPremium, discountedAnnual, request.getInstallments());

        return PremiumQuoteResponse.builder()
                .customerId(customerId)
                .tier(customer.getTier())
                .annualPremium(annualPremium)
                .discountRate(discountRate)
                .discountAmount(discountAmount)
                .discountedAnnualPremium(discountedAnnual)
                .installments(request.getInstallments())
                .installmentAmount(installmentAmount)
                .build();
    }
}
