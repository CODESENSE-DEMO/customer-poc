package com.unionplace.customer.memo.spec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CustomerMemoResponse {

    private Long id;
    private String customerNo;
    private MemoCategory category;
    private String title;
    private String content;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CustomerMemoResponse from(CustomerMemo memo) {
        return CustomerMemoResponse.builder()
                .id(memo.getId())
                .customerNo(memo.getCustomerNo())
                .category(memo.getCategory())
                .title(memo.getTitle())
                .content(memo.getContent())
                .createdBy(memo.getCreatedBy())
                .createdAt(memo.getCreatedAt())
                .updatedAt(memo.getUpdatedAt())
                .build();
    }
}
