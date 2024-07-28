package com.example.tabletop.commons.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String path = request.getServletPath();
		if(path.equals("/api/auth/token/refresh")) {
            filterChain.doFilter(request, response);
            return;
        } else {
        	String accessToken = getTokenFromRequest(request);
    		if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
    			UsernamePasswordAuthenticationToken authentication = getAuthenticationFromToken(accessToken);
    			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    			SecurityContextHolder.getContext().setAuthentication(authentication);
    		}
        }
		filterChain.doFilter(request, response);
	}

	private String getTokenFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private UsernamePasswordAuthenticationToken getAuthenticationFromToken(String accessToken) {
		String loginId = jwtTokenProvider.getUsernameFromToken(accessToken);
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginId);

		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}
}
