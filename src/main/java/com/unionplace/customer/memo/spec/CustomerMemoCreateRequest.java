package com.unionplace.customer.memo.spec;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomerMemoCreateRequest {

    @NotNull(message = "메모 분류는 필수입니다.")
    private MemoCategory category;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = CustomerMemo.MAX_CONTENT_LENGTH, message = "내용은 1000자를 초과할 수 없습니다.")
    private String content;
}
