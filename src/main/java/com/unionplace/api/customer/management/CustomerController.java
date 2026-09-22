package com.unionplace.api.customer.management;

import com.unionplace.customer.management.spec.CustomerCreateRequest;
import com.unionplace.customer.management.spec.CustomerResponse;
import com.unionplace.customer.management.spec.CustomerSearchRequest;
import com.unionplace.customer.management.spec.CustomerService;
import com.unionplace.customer.management.spec.CustomerStatus;
import com.unionplace.customer.management.spec.CustomerUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 고객 API. 반환값은 프레임워크가 자동으로 ApiResponse 로 감싼다.
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody CustomerCreateRequest request) {
        return customerService.createCustomer(request);
    }

    // [DEFECT-024] 인가 누락: 고객 상세 조회 엔드포인트에 인가 체크 없음
    // - 인증만 통과하면 다른 고객의 PII(주민등록번호 포함) 무단 조회 가능 (IDOR)
    // - @PreAuthorize 또는 본인/관리자 권한 검증 필요
    @GetMapping("/{id}")
    public CustomerResponse get(@PathVariable Long id) {
        return customerService.getCustomer(id);
    }

    @GetMapping
    public List<CustomerResponse> getAll() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/search")
    public List<CustomerResponse> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) CustomerStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDateTo) {
        CustomerSearchRequest request = CustomerSearchRequest.builder()
                .nameKeyword(name)
                .status(status)
                .birthDateFrom(birthDateFrom)
                .birthDateTo(birthDateTo)
                .build();
        return customerService.searchCustomers(request);
    }

    @GetMapping("/count")
    public Long countByStatus(@RequestParam CustomerStatus status) {
        return customerService.countByStatus(status);
    }

    // [DEFECT-025] 입력 검증 누락: @Valid 미적용
    // - email/phone 형식 검증 어노테이션이 무시되어 서비스로 그대로 전달됨
    @PutMapping("/{id}/contact")
    public CustomerResponse updateContact(
            @PathVariable Long id,
            @RequestBody CustomerUpdateRequest request) {
        return customerService.updateContact(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void withdraw(@PathVariable Long id) {
        customerService.withdrawCustomer(id);
    }
}
