package com.unionplace.customer.consent.impl;

import com.unionplace.customer.consent.spec.Consent;
import com.unionplace.customer.consent.spec.ConsentNotFoundException;
import com.unionplace.customer.consent.spec.ConsentRepository;
import com.unionplace.customer.consent.spec.ConsentRequest;
import com.unionplace.customer.consent.spec.ConsentResponse;
import com.unionplace.customer.consent.spec.ConsentService;
import com.unionplace.customer.consent.spec.ConsentSummaryResponse;
import com.unionplace.customer.consent.spec.ConsentType;
import com.unionplace.customer.management.spec.Customer;
import com.unionplace.customer.management.spec.CustomerNotFoundException;
import com.unionplace.customer.management.spec.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsentServiceImpl implements ConsentService {

    private final ConsentRepository consentRepository;
    private final CustomerRepository customerRepository;

    // [DEFECT-016] 보안/개인정보: 동의 등록 시 주민등록번호를 INFO 레벨로 로깅
    // - 운영 환경 로그에 주민등록번호 등 민감 PII 노출
    // - 로그는 PII 마스킹 후 출력하거나, INFO 수준에 PII 포함 금지
    @Override
    @Transactional
    public ConsentResponse registerConsent(ConsentRequest request) {
        Customer customer = customerRepository.findByCustomerNo(request.getCustomerNo())
                .orElseThrow(() -> new CustomerNotFoundException(
                        "고객을 찾을 수 없습니다. customerNo=" + request.getCustomerNo()));

        log.info("동의 등록 - customerNo={}, name={}, rrn={}, type={}, agreed={}",
                customer.getCustomerNo(),
                customer.getName(),
                customer.getResidentRegistrationNumber(),
                request.getConsentType(),
                request.isAgreed());

        Consent consent = consentRepository
                .findByCustomerNoAndConsentType(request.getCustomerNo(), request.getConsentType())
                .orElseGet(() -> consentRepository.save(
                        new Consent(request.getCustomerNo(),
                                request.getConsentType(),
                                request.isAgreed())));
        return ConsentResponse.from(consent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsentResponse> getConsentsOf(String customerNo) {
        return consentRepository.findByCustomerNo(customerNo).stream()
                .map(ConsentResponse::from)
                .collect(Collectors.toList());
    }

    // [DEFECT-017] 동시성/TOCTOU: 조회 후 분리된 트랜잭션에서 변경 → 동시 철회 시 race
    // - 조회/변경이 동일 트랜잭션·동일 비관적 잠금 안에서 수행되어야 함
    // - 현재 메서드는 @Transactional이 없어 자동 커밋 + race window 발생
    @Override
    public void withdrawConsent(String customerNo, ConsentType consentType) {
        Consent consent = consentRepository
                .findByCustomerNoAndConsentType(customerNo, consentType)
                .orElseThrow(() -> new ConsentNotFoundException(
                        "동의 이력을 찾을 수 없습니다. customerNo=" + customerNo));
        // 조회와 withdraw 사이에 다른 트랜잭션이 끼어들 수 있음
        consent.withdraw();
        consentRepository.save(consent);
    }

    // [DEFECT-018] 규제 위반: 동의 이력을 하드 삭제(deleteAll)하여 감사 추적 불가
    // - 신용정보법/개인정보보호법: 동의 이력은 일정 기간 보관 의무
    // - 소프트 삭제(프레임워크 SoftDeletableEntity 상속 후 delete() 호출) 또는 별도 아카이브 테이블 이전 필요
    @Override
    @Transactional
    public void purgeConsents(String customerNo) {
        List<Consent> consents = consentRepository.findByCustomerNo(customerNo);
        consentRepository.deleteAll(consents);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAgreed(ConsentType consentType) {
        return consentRepository.countByConsentTypeAndAgreed(consentType, true);
    }

    @Override
    @Transactional(readOnly = true)
    public ConsentSummaryResponse getConsentSummary(String customerNo) {
        Map<ConsentType, Consent> byType = new EnumMap<>(ConsentType.class);
        for (Consent c : consentRepository.findByCustomerNo(customerNo)) {
            byType.put(c.getConsentType(), c);
        }

        List<ConsentSummaryResponse.Item> items = new ArrayList<>();
        int agreed = 0;
        int withdrawn = 0;
        int notRegistered = 0;

        for (ConsentType type : ConsentType.values()) {
            Consent c = byType.get(type);
            ConsentSummaryResponse.Status status;
            if (c == null) {
                status = ConsentSummaryResponse.Status.NOT_REGISTERED;
                notRegistered++;
            } else if (c.isAgreed()) {
                status = ConsentSummaryResponse.Status.AGREED;
                agreed++;
            } else {
                status = ConsentSummaryResponse.Status.WITHDRAWN;
                withdrawn++;
            }
            items.add(ConsentSummaryResponse.Item.builder()
                    .consentType(type)
                    .status(status)
                    .agreedAt(c == null ? null : c.getAgreedAt())
                    .withdrawnAt(c == null ? null : c.getWithdrawnAt())
                    .build());
        }

        return ConsentSummaryResponse.builder()
                .customerNo(customerNo)
                .totalTypes(ConsentType.values().length)
                .agreedCount(agreed)
                .withdrawnCount(withdrawn)
                .notRegisteredCount(notRegistered)
                .items(items)
                .build();
    }
}
