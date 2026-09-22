package com.unionplace.customer.memo.impl;

import com.unionplace.customer.management.spec.CustomerNotFoundException;
import com.unionplace.customer.management.spec.CustomerRepository;
import com.unionplace.customer.memo.spec.CustomerMemo;
import com.unionplace.customer.memo.spec.CustomerMemoCreateRequest;
import com.unionplace.customer.memo.spec.CustomerMemoNotFoundException;
import com.unionplace.customer.memo.spec.CustomerMemoRepository;
import com.unionplace.customer.memo.spec.CustomerMemoResponse;
import com.unionplace.customer.memo.spec.CustomerMemoService;
import com.unionplace.customer.memo.spec.CustomerMemoUpdateRequest;
import com.unionplace.customer.memo.spec.MemoCategory;
import io.union.core.response.PageResponse;
import io.union.data.support.PageResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 고객 상담 메모 서비스.
 *
 * <p>메모 본문에는 개인정보를 남기지 않는 것을 원칙으로 하며, 로그에도 식별자만 남긴다(LOG-05).
 * 삭제는 감사 추적을 위해 소프트 삭제로 처리한다(FW-03).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerMemoServiceImpl implements CustomerMemoService {

    private final CustomerMemoRepository customerMemoRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerMemoResponse createMemo(String customerNo, CustomerMemoCreateRequest request) {
        verifyCustomerExists(customerNo);

        CustomerMemo memo = customerMemoRepository.save(new CustomerMemo(
                customerNo,
                request.getCategory(),
                request.getTitle(),
                request.getContent()));

        log.info("상담 메모 등록 - customerNo={}, memoId={}, category={}",
                memo.getCustomerNo(), memo.getId(), memo.getCategory());
        return CustomerMemoResponse.from(memo);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerMemoResponse getMemo(String customerNo, Long memoId) {
        return CustomerMemoResponse.from(findOwnedMemo(customerNo, memoId));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CustomerMemoResponse> getMemosOf(
            String customerNo, MemoCategory category, Pageable pageable) {
        Page<CustomerMemo> memos = (category == null)
                ? customerMemoRepository.findByCustomerNoOrderByIdDesc(customerNo, pageable)
                : customerMemoRepository.findByCustomerNoAndCategoryOrderByIdDesc(customerNo, category, pageable);
        return PageResponses.from(memos.map(CustomerMemoResponse::from));
    }

    @Override
    @Transactional
    public CustomerMemoResponse updateMemo(String customerNo, Long memoId, CustomerMemoUpdateRequest request) {
        CustomerMemo memo = findOwnedMemo(customerNo, memoId);
        memo.update(request.getCategory(), request.getTitle(), request.getContent());
        log.info("상담 메모 수정 - customerNo={}, memoId={}", customerNo, memoId);
        return CustomerMemoResponse.from(memo);
    }

    @Override
    @Transactional
    public void deleteMemo(String customerNo, Long memoId) {
        CustomerMemo memo = findOwnedMemo(customerNo, memoId);
        memo.softDelete();
        log.info("상담 메모 삭제 - customerNo={}, memoId={}", customerNo, memoId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMemosOf(String customerNo) {
        return customerMemoRepository.countByCustomerNo(customerNo);
    }

    private CustomerMemo findOwnedMemo(String customerNo, Long memoId) {
        CustomerMemo memo = customerMemoRepository.findById(memoId)
                .orElseThrow(() -> new CustomerMemoNotFoundException(
                        "상담 메모를 찾을 수 없습니다. memoId=" + memoId));
        memo.verifyOwnedBy(customerNo);
        return memo;
    }

    private void verifyCustomerExists(String customerNo) {
        if (customerRepository.findByCustomerNo(customerNo).isEmpty()) {
            throw new CustomerNotFoundException("고객을 찾을 수 없습니다. customerNo=" + customerNo);
        }
    }
}
