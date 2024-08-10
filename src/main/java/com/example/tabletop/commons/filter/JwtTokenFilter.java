package com.example.tabletop.commons.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.tabletop.auth.exception.TokenException;
import com.example.tabletop.auth.service.CustomUserDetailsService;
import com.example.tabletop.commons.config.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter { 
	// 토큰 검증을 2번의 과정을 거침. 
	// 1, 서명키가 유효한지 검증 
	// 2. 토큰 안에 저장되어 있는 로그인 id를 꺼내서 해당 id를 loadUserByUsername를 통해 db에 로그인id가 존재하는지 검증
	private final JwtTokenProvider jwtTokenProvider;
	private final CustomUserDetailsService customUserDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, TokenException {
		String path = request.getServletPath();

		// 익명 접근이 허용된 경로 목록
		List<String> permitAllPaths = List.of("/api/sse/notify/", "/api/orders/", "/api/stores/", "/consumer/", "/api/auth/", "/api/mail/", "/api/sellers/signup", "/api/sellers/exists");

		if (permitAllPaths.stream().anyMatch(path::startsWith)) {
			filterChain.doFilter(request, response);
			return;
		}

		String accessToken = getTokenFromRequest(request);
		if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
			UsernamePasswordAuthenticationToken authentication = getAuthenticationFromToken(accessToken);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

	private String getTokenFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private UsernamePasswordAuthenticationToken getAuthenticationFromToken(String accessToken) {
		String loginId = jwtTokenProvider.getUserLoginIdFromToken(accessToken);
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginId);

		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}
}
