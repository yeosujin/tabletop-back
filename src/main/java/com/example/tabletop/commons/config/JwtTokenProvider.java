package com.example.tabletop.commons.config;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.example.tabletop.auth.service.CustomUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.accessTokenExpirationTime}")
	private long accessTokenValidity;

	@Value("${jwt.refreshTokenExpirationTime}")
	private long refreshTokenValidity;

	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	public String createAccessToken(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Date expiryDate = new Date(new Date().getTime() + accessTokenValidity);

        return Jwts.builder()
                .setSubject(customUserDetails.getUsername())
                .claim("seller-id", customUserDetails.getSeller().getId())
                .claim("seller-username", customUserDetails.getSeller().getUsername())
                .claim("seller-email", customUserDetails.getSeller().getEmail())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
	
	public String createRefreshToken(Authentication authentication) {
		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        
        return Jwts.builder()
                .setSubject(customUserDetails.getUsername())
                .claim("seller-id", customUserDetails.getSeller().getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

	public String getUserLoginIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }
	
	public boolean validateToken(String token) {
        try {
            Jwts
            	.parserBuilder()
            	.setSigningKey(getSigningKey()) // 서명키로 검증
            	.build()
            	.parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
        	return false;
       }
    }
}