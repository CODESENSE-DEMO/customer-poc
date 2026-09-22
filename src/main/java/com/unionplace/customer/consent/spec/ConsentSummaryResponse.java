package com.unionplace.customer.consent.spec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ConsentSummaryResponse {

    private String customerNo;
    private int totalTypes;
    private int agreedCount;
    private int withdrawnCount;
    private int notRegisteredCount;
    private List<Item> items;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Item {
        private ConsentType consentType;
        private Status status;
        private LocalDateTime agreedAt;
        private LocalDateTime withdrawnAt;
    }

    public enum Status {
        AGREED,
        WITHDRAWN,
        NOT_REGISTERED
    }
}
