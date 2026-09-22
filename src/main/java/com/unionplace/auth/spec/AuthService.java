package com.unionplace.auth.spec;

import io.union.security.jwt.TokenPair;

public interface AuthService {

    TokenPair login(LoginRequest request);

    TokenPair refresh(String refreshToken);
}
