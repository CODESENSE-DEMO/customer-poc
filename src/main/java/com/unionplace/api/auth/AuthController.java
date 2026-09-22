package com.unionplace.api.auth;

import com.unionplace.auth.spec.AuthService;
import com.unionplace.auth.spec.LoginRequest;
import io.union.security.annotation.CurrentUser;
import io.union.security.jwt.TokenPair;
import io.union.security.user.UnionUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 운영자 로그인 / 토큰 갱신. union.security.permit-all 에 의해 인증 없이 접근 가능. */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    public record RefreshRequest(@NotBlank String refreshToken) {}

    private final AuthService authService;

    @PostMapping("/login")
    public TokenPair login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public TokenPair refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @GetMapping("/me")
    public UnionUser me(@CurrentUser UnionUser user) {
        return user;
    }
}
