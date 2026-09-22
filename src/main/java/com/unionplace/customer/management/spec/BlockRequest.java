package com.unionplace.customer.management.spec;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BlockRequest {

    @NotNull(message = "차단 사유는 필수입니다.")
    private BlockReason reason;

    @Size(max = 500, message = "비고는 500자 이내로 작성해야 합니다.")
    private String memo;
}
