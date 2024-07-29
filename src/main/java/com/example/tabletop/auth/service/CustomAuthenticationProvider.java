package com.example.tabletop.auth.service;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.tabletop.auth.exception.InvalidPasswordException;
import com.example.tabletop.seller.exception.SellerNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	
	private final PasswordEncoder passwordEncoder;
	private final CustomUserDetailsService userDetailsService;

	@Override
	public Authentication authenticate(Authentication authentication) throws SellerNotFoundException, InvalidPasswordException, AuthenticationException {
		String loginId = (String) authentication.getPrincipal();
		String password = (String) authentication.getCredentials();

		UserDetails userDetails = userDetailsService.loadUserByUsername(loginId);
		if (!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
		}
		
		return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}
}