package com.example.tabletop.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.example.tabletop.auth.dto.LoginRequestDTO;
import com.example.tabletop.auth.dto.LoginResponseDTO;
import com.example.tabletop.auth.exception.InvalidPasswordException;
import com.example.tabletop.auth.exception.LogoutException;
import com.example.tabletop.auth.exception.RefreshTokenException;
import com.example.tabletop.commons.config.JwtTokenProvider;
import com.example.tabletop.seller.entity.Seller;
import com.example.tabletop.seller.exception.SellerNotFoundException;
import com.example.tabletop.seller.repository.SellerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final AuthenticationManager authenticationManager;
	private final CustomUserDetailsService customUserDetailsService;
	private final SellerRepository sellerRepository;

	public LoginResponseDTO login(LoginRequestDTO loginRequest) throws SellerNotFoundException, InvalidPasswordException, AuthenticationException {
		log.info("로그인 시도: {}", loginRequest.getLoginId());
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword()));

		Seller seller = ((CustomUserDetails) authentication.getPrincipal()).getSeller();
		String accessToken = jwtTokenProvider.createAccessToken(authentication);
		String refreshToken = jwtTokenProvider.createRefreshToken();

		seller.setRefreshToken(refreshToken);
		sellerRepository.save(seller);

		return new LoginResponseDTO(accessToken);
	}
	
	public void logout(String loginId) throws SellerNotFoundException, LogoutException {
		log.info("로그아웃 시도: {}", loginId);
		Seller seller = sellerRepository.findByLoginId(loginId)
				.orElseThrow(() -> new SellerNotFoundException("해당 로그인 ID로 판매자를 찾을 수 없습니다."));
		
		if (seller.getRefreshToken() == null) {
			throw new LogoutException("이미 로그아웃된 상태입니다.");
		}
		
		seller.setRefreshToken(null);
		sellerRepository.save(seller);
	}

	public String refreshAccessToken(String accessToken) throws RefreshTokenException {
		log.info("AccessToken 갱신 시도: {}", accessToken);
		String loginId = jwtTokenProvider.getUsernameFromToken(accessToken);
		CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(loginId);

		if (jwtTokenProvider.validateToken(userDetails.getSeller().getRefreshToken())) {
			return jwtTokenProvider.createAccessToken(new UsernamePasswordAuthenticationToken(userDetails,
					userDetails.getPassword(), userDetails.getAuthorities()));
		} else {
			log.error("AccessToken 갱신 실패: {}", accessToken);
			throw new RefreshTokenException("유효하지 않은 리프레시 토큰입니다.");
		}		
	}
}