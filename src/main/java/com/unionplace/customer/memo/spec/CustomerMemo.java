package com.unionplace.customer.memo.spec;

import io.union.core.util.Assert;
import io.union.data.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

/**
 * 고객 상담 메모. 상담 이력은 보관 의무가 있어 하드 삭제 대신
 * 프레임워크의 {@link SoftDeletableEntity} 를 상속해 소프트 삭제한다(FW-03).
 */
@Entity
@Table(
        name = "CUSTOMER_MEMO",
        indexes = @Index(name = "IDX_CUSTOMER_MEMO_CUSTOMER_NO", columnList = "CUSTOMER_NO")
)
@SQLRestriction("deleted = false")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerMemo extends SoftDeletableEntity {

    public static final int MAX_CONTENT_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CUSTOMER_NO", nullable = false, length = 20)
    private String customerNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "CATEGORY", nullable = false, length = 30)
    private MemoCategory category;

    @Column(name = "TITLE", nullable = false, length = 100)
    private String title;

    @Column(name = "CONTENT", nullable = false, length = MAX_CONTENT_LENGTH)
    private String content;

    public CustomerMemo(String customerNo, MemoCategory category, String title, String content) {
        this.customerNo = customerNo;
        this.category = category;
        this.title = title;
        this.content = content;
    }

    public void update(MemoCategory category, String title, String content) {
        this.category = category;
        this.title = title;
        this.content = content;
    }

    /** 다른 고객의 메모에 접근하는 것을 막는다. */
    public void verifyOwnedBy(String customerNo) {
        Assert.isTrue(this.customerNo.equals(customerNo), CustomerMemoErrorCode.MEMO_NOT_OWNED);
    }

    public void softDelete() {
        Assert.isTrue(!isDeleted(), CustomerMemoErrorCode.MEMO_ALREADY_DELETED);
        delete();
    }
}
