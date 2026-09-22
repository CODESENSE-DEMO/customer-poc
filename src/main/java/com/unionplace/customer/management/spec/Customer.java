package com.unionplace.customer.management.spec;

import io.union.core.util.Assert;
import io.union.data.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "CUSTOMER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CUSTOMER_NO", nullable = false, unique = true, length = 20)
    private String customerNo;

    @Column(name = "NAME", nullable = false, length = 50)
    private String name;

    // [DEFECT-007] 개인정보 보호: 주민등록번호 평문 저장
    // - 금융사 표준: 양방향 암호화(AES-256) 후 저장, 일부 자리 별도 보관
    // - 평문 저장은 신용정보법/개인정보보호법 위반 소지
    @Column(name = "RESIDENT_REGISTRATION_NUMBER", nullable = false, length = 14)
    private String residentRegistrationNumber;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "PHONE_NUMBER", length = 20)
    private String phoneNumber;

    @Column(name = "BIRTH_DATE", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private CustomerStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIER", nullable = false, length = 20)
    private CustomerTier tier;
    @Column(name = "BLOCKED", nullable = false)
    private boolean blocked;

    @Enumerated(EnumType.STRING)
    @Column(name = "BLOCK_REASON", length = 30)
    private BlockReason blockReason;

    public Customer(String customerNo, String name, String residentRegistrationNumber,
                    String email, String phoneNumber, LocalDate birthDate) {
        this.customerNo = customerNo;
        this.name = name;
        this.residentRegistrationNumber = residentRegistrationNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.status = CustomerStatus.ACTIVE;
        this.tier = CustomerTier.BASIC;
    }

    public void changeTier(CustomerTier newTier) {
        Assert.notNull(newTier, "등급은 필수입니다.");
        Assert.isTrue(this.status != CustomerStatus.WITHDRAWN, CustomerErrorCode.TIER_CHANGE_NOT_ALLOWED);
        this.tier = newTier;
    }

    public boolean isPremiumOrAbove() {
        return this.tier == CustomerTier.PREMIUM || this.tier == CustomerTier.VIP;
    }

    public void block(BlockReason reason) {
        Assert.notNull(reason, "차단 사유는 필수입니다.");
        this.blocked = true;
        this.blockReason = reason;
    }

    public void unblock() {
        this.blocked = false;
        this.blockReason = null;
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public void updateContact(String email, String phoneNumber) {
        if (email != null && !email.isBlank()) {
            this.email = email;
        }
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            this.phoneNumber = phoneNumber;
        }
    }

    public void markDormant() {
        Assert.isTrue(this.status != CustomerStatus.WITHDRAWN, CustomerErrorCode.CUSTOMER_ALREADY_WITHDRAWN);
        this.status = CustomerStatus.DORMANT;
    }

    public void withdraw() {
        Assert.isTrue(this.status != CustomerStatus.WITHDRAWN, CustomerErrorCode.CUSTOMER_ALREADY_WITHDRAWN);
        this.status = CustomerStatus.WITHDRAWN;
    }
}
