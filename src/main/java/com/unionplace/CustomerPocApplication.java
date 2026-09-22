package com.unionplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 고객 관리 PoC 애플리케이션.
 * 표준 응답 포맷, 예외 처리, TraceId, JPA Auditing, JWT 인증은 union-starter 자동 설정이 담당한다.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class CustomerPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerPocApplication.class, args);
    }
}
