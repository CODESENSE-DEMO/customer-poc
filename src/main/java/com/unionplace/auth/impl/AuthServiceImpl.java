package com.unionplace.auth.impl;

import com.unionplace.auth.spec.AuthErrorCode;
import com.unionplace.auth.spec.AuthService;
import com.unionplace.auth.spec.LoginRequest;
import com.unionplace.auth.spec.OperatorProperties;
import io.union.core.exception.BusinessException;
import io.union.core.util.Assert;
import io.union.security.jwt.JwtTokenProvider;
import io.union.security.jwt.TokenPair;
import io.union.security.user.UnionUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final OperatorProperties operatorProperties;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Override
    public TokenPair login(LoginRequest request) {
        OperatorProperties.Operator operator = operatorProperties.getOperators().stream()
                .filter(o -> o.getUsername().equals(request.getUsername()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_CREDENTIALS));

        Assert.isTrue(passwordEncoder.matches(request.getPassword(), operator.getPasswordHash()),
                AuthErrorCode.INVALID_CREDENTIALS);

        log.info("운영자 로그인. operatorId={}, username={}", operator.getId(), operator.getUsername());
        return tokenProvider.issue(new UnionUser(operator.getId(), operator.getUsername(), operator.getRoles()));
    }

    @Override
    public TokenPair refresh(String refreshToken) {
        return tokenProvider.refresh(refreshToken);
    }
}
