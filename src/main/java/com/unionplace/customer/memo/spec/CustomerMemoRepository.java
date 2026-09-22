package com.unionplace.customer.memo.spec;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerMemoRepository extends JpaRepository<CustomerMemo, Long> {

    Page<CustomerMemo> findByCustomerNoOrderByIdDesc(String customerNo, Pageable pageable);

    Page<CustomerMemo> findByCustomerNoAndCategoryOrderByIdDesc(
            String customerNo, MemoCategory category, Pageable pageable);

    long countByCustomerNo(String customerNo);
}
