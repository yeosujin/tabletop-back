package com.example.tabletop.commons.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.accessTokenExpirationTime}")
    private long accessTokenValidity;

    @Value("${jwt.refreshTokenExpirationTime}")
    private long refreshTokenValidity;
}