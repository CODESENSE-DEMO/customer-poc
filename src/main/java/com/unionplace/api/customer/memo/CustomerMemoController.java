package com.unionplace.api.customer.memo;

import com.unionplace.customer.memo.spec.CustomerMemoCreateRequest;
import com.unionplace.customer.memo.spec.CustomerMemoResponse;
import com.unionplace.customer.memo.spec.CustomerMemoService;
import com.unionplace.customer.memo.spec.CustomerMemoUpdateRequest;
import com.unionplace.customer.memo.spec.MemoCategory;
import io.union.core.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers/{customerNo}/memos")
@RequiredArgsConstructor
public class CustomerMemoController {

    private final CustomerMemoService customerMemoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerMemoResponse create(
            @PathVariable String customerNo,
            @Valid @RequestBody CustomerMemoCreateRequest request) {
        return customerMemoService.createMemo(customerNo, request);
    }

    @GetMapping
    public PageResponse<CustomerMemoResponse> getMemos(
            @PathVariable String customerNo,
            @RequestParam(required = false) MemoCategory category,
            @PageableDefault(size = 20) Pageable pageable) {
        return customerMemoService.getMemosOf(customerNo, category, pageable);
    }

    @GetMapping("/{memoId}")
    public CustomerMemoResponse getMemo(
            @PathVariable String customerNo,
            @PathVariable Long memoId) {
        return customerMemoService.getMemo(customerNo, memoId);
    }

    @GetMapping("/count")
    public Long count(@PathVariable String customerNo) {
        return customerMemoService.countMemosOf(customerNo);
    }

    @PutMapping("/{memoId}")
    public CustomerMemoResponse update(
            @PathVariable String customerNo,
            @PathVariable Long memoId,
            @Valid @RequestBody CustomerMemoUpdateRequest request) {
        return customerMemoService.updateMemo(customerNo, memoId, request);
    }

    @DeleteMapping("/{memoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(
            @PathVariable String customerNo,
            @PathVariable Long memoId) {
        customerMemoService.deleteMemo(customerNo, memoId);
    }
}
