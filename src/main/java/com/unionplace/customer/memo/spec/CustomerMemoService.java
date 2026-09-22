package com.unionplace.customer.memo.spec;

import io.union.core.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CustomerMemoService {

    CustomerMemoResponse createMemo(String customerNo, CustomerMemoCreateRequest request);

    CustomerMemoResponse getMemo(String customerNo, Long memoId);

    PageResponse<CustomerMemoResponse> getMemosOf(String customerNo, MemoCategory category, Pageable pageable);

    CustomerMemoResponse updateMemo(String customerNo, Long memoId, CustomerMemoUpdateRequest request);

    void deleteMemo(String customerNo, Long memoId);

    long countMemosOf(String customerNo);
}
