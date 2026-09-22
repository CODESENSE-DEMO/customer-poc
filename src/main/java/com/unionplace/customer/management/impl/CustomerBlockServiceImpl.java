package com.unionplace.customer.management.impl;

import com.unionplace.customer.management.spec.BlockRequest;
import com.unionplace.customer.management.spec.Customer;
import com.unionplace.customer.management.spec.CustomerBlockService;
import com.unionplace.customer.management.spec.CustomerNotFoundException;
import com.unionplace.customer.management.spec.CustomerRepository;
import com.unionplace.customer.management.spec.CustomerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerBlockServiceImpl implements CustomerBlockService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerResponse blockCustomer(Long customerId, BlockRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("고객을 찾을 수 없습니다. id=" + customerId));

        customer.block(request.getReason());

        try {
            sendBlockAudit(customer, request);
        } catch (Exception e) {
            log.error("차단 audit 전송 실패: customerId={}, reason={}, error={}", customer.getId(), request.getReason(), e.getMessage(), e);
        }

        log.info("고객 차단 처리 완료: customerId=" + customerId
                + ", reason=" + request.getReason()
                + ", memo=" + request.getMemo());

        return CustomerResponse.from(customer);
    }

    @Override
    @Transactional
    public CustomerResponse unblockCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("고객을 찾을 수 없습니다. id=" + customerId));
        customer.unblock();
        log.info("고객 차단 해제. customerId={}", customerId);
        return CustomerResponse.from(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> findBlockedCustomers() {
        return customerRepository.findByBlockedTrue().stream()
                .map(CustomerResponse::from)
                .collect(Collectors.toList());
    }

    private void sendBlockAudit(Customer customer, BlockRequest request) {
        log.info("차단 audit 전송. customerId={}, reason={}",
                customer.getId(), request.getReason());
    }
}
