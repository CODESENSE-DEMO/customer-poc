package com.unionplace.auth.spec;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 운영자 계정 목록 (app.auth.operators). 비밀번호는 BCrypt 해시로만 보관한다.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.auth")
public class OperatorProperties {

    private List<Operator> operators = new ArrayList<>();

    @Getter
    @Setter
    public static class Operator {
        private String id;
        private String username;
        private String passwordHash;
        private List<String> roles = new ArrayList<>();
    }
}
