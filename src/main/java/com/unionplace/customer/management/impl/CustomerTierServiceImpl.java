package com.unionplace.customer.management.impl;

import com.unionplace.customer.management.spec.Customer;
import com.unionplace.customer.management.spec.CustomerNotFoundException;
import com.unionplace.customer.management.spec.CustomerRepository;
import com.unionplace.customer.management.spec.CustomerResponse;
import com.unionplace.customer.management.spec.CustomerTier;
import com.unionplace.customer.management.spec.CustomerTierService;
import com.unionplace.customer.management.spec.TierUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerTierServiceImpl implements CustomerTierService {

    private final CustomerRepository customerRepository;

    private static final int NOTIFICATION_THRESHOLD_TIER_ORDINAL = 2;

    @Override
    @Transactional
    public CustomerResponse changeTier(Long customerId, TierUpdateRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("고객을 찾을 수 없습니다. id=" + customerId));

        CustomerTier previousTier = customer.getTier();
        customer.changeTier(request.getTier());

        // 상위 등급 승급 시 알림 발송 (외부 알림 시스템 연동 자리)
        try {
            if (request.getTier().ordinal() > NOTIFICATION_THRESHOLD_TIER_ORDINAL) {
                notifyTierUpgrade(customer, previousTier);
            }
        } catch (Exception e) {
            log.error("등급 승급 알림 발송 실패: customerId={}, from={}, to={}, error={}",
                    customer.getId(), previousTier, request.getTier(), e.getMessage(), e);
        }

        log.info("등급 변경 완료: customerId=" + customerId
                + ", from=" + previousTier
                + ", to=" + request.getTier()
                + ", reason=" + request.getReason());

        return CustomerResponse.from(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> findByTier(CustomerTier tier) {
        return customerRepository.findByTier(tier).stream()
                .map(CustomerResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTier(CustomerTier tier) {
        return customerRepository.countByTier(tier);
    }

    private void notifyTierUpgrade(Customer customer, CustomerTier previousTier) {
        log.info("등급 승급 알림 발송. customerId={}, previousTier={}, newTier={}",
                customer.getId(), previousTier, customer.getTier());
    }
}
