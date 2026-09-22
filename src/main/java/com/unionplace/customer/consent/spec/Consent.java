package com.unionplace.customer.consent.spec;

import io.union.core.util.Assert;
import io.union.data.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "CONSENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Consent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CUSTOMER_NO", nullable = false, length = 20)
    private String customerNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "CONSENT_TYPE", nullable = false, length = 30)
    private ConsentType consentType;

    @Column(name = "AGREED", nullable = false)
    private boolean agreed;

    @Column(name = "AGREED_AT")
    private LocalDateTime agreedAt;

    @Column(name = "WITHDRAWN_AT")
    private LocalDateTime withdrawnAt;

    public Consent(String customerNo, ConsentType consentType, boolean agreed) {
        this.customerNo = customerNo;
        this.consentType = consentType;
        this.agreed = agreed;
        if (agreed) {
            this.agreedAt = LocalDateTime.now();
        }
    }

    public void withdraw() {
        Assert.isTrue(this.agreed, ConsentErrorCode.CONSENT_ALREADY_WITHDRAWN);
        this.agreed = false;
        this.withdrawnAt = LocalDateTime.now();
    }
}
