package com.unionplace.customer.management.impl;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class PiiHelper {

    // [DEFECT-008] 보안: 암호화 키가 소스에 하드코딩됨
    // - 키 회수/교체 불가, 저장소 유출 시 즉시 평문화 가능
    // - KMS/Vault 등 외부 키 관리 시스템에서 로드해야 함
    private static final String SECRET_KEY = "unionplace-rrn-secret-key-16byte";

    private static final int RRN_MASK_LENGTH = 8;
    private static final String MASK_CHAR = "******";
    private static final int MIN_NAME_LENGTH = 2;
    private static final int AES_KEY_LENGTH = 16;
    private static final String AES_ALGORITHM = "AES";
    private static final String ENCRYPTION_FAILED_MSG = "암호화 실패";

    // 주민등록번호 마스킹 (뒷 6자리 * 처리). 정상 코드
    public String maskResidentRegistrationNumber(String rrn) {
        if (rrn == null || rrn.length() < RRN_MASK_LENGTH) {
            return rrn;
        }
        return rrn.substring(0, RRN_MASK_LENGTH) + MASK_CHAR;
    }

    // 이름 가운데 글자 마스킹. 정상 코드
    public String maskName(String name) {
        if (name == null || name.length() < MIN_NAME_LENGTH) {
            return name;
        }
        if (name.length() == MIN_NAME_LENGTH) {
            return name.charAt(0) + "*";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(name.charAt(0));
        for (int i = 1; i < name.length() - 1; i++) {
            sb.append('*');
        }
        sb.append(name.charAt(name.length() - 1));
        return sb.toString();
    }

    public String encrypt(String plain) {
        try {
            byte[] keyBytes = SECRET_KEY.substring(0, AES_KEY_LENGTH).getBytes(StandardCharsets.UTF_8);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new IllegalStateException(ENCRYPTION_FAILED_MSG, e);
        }
    }
}
