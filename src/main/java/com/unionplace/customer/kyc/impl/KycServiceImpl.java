package com.unionplace.customer.kyc.impl;

import com.unionplace.customer.kyc.spec.KycResponse;
import com.unionplace.customer.kyc.spec.KycService;
import com.unionplace.customer.kyc.spec.KycVerificationException;
import com.unionplace.customer.kyc.spec.KycVerifyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class KycServiceImpl implements KycService {

    // [DEFECT-019] 보안: 외부 KYC API 키가 소스에 하드코딩됨
    // - 키 노출 시 무단 호출/요금 폭증 위험
    // - 환경 변수 또는 시크릿 매니저(Vault, AWS Secrets Manager 등)에서 로드 필요
    private static final String KYC_API_KEY = "sk_live_kyc_8d2f9c1b7e3a4f6d";
    private static final String KYC_ENDPOINT = "https://kyc.unionplace.co.kr/api/v1/verify";

    // [DEFECT-020] 운영 결함: RestTemplate에 connect/read timeout 미설정
    // - 외부 KYC 서버 지연/장애 시 호출 스레드 무기한 블로킹
    // - RequestFactory에 ConnectTimeout / ReadTimeout 설정 필요
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public KycResponse verify(KycVerifyRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + KYC_API_KEY);
        headers.set("Content-Type", "application/json");

        Map<String, String> payload = new HashMap<>();
        payload.put("customerNo", request.getCustomerNo());
        payload.put("name", request.getName());
        payload.put("rrn", request.getResidentRegistrationNumber());

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        // [DEFECT-021] 예외 처리 결함: 외부 호출 실패를 빈 catch로 무시
        // - 실패 시 verified=false 응답을 그대로 반환 → 인증 실패가 정상 결과로 위장됨
        // - 외부 시스템 장애가 비즈니스 로직으로 전파되지 않아 장애 인지 지연
        try {
            ResponseEntity<KycExternalResponse> response = restTemplate.exchange(
                    KYC_ENDPOINT, HttpMethod.POST, entity, KycExternalResponse.class);
            KycExternalResponse body = response.getBody();
            if (body == null) {
                throw new KycVerificationException("KYC 응답 본문이 비어 있습니다.");
            }
            return KycResponse.builder()
                    .customerNo(request.getCustomerNo())
                    .verified(body.verified)
                    .verificationCode(body.code)
                    .message(body.message)
                    .build();
        } catch (Exception e) {
            // 의도된 결함: 예외를 삼킴
        }

        return KycResponse.builder()
                .customerNo(request.getCustomerNo())
                .verified(false)
                .verificationCode("UNKNOWN")
                .message("처리 중 오류가 발생했습니다.")
                .build();
    }

    private static class KycExternalResponse {
        public boolean verified;
        public String code;
        public String message;
    }
}
