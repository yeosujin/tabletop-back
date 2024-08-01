package com.example.tabletop.auth.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabletop.auth.dto.LoginRequestDTO;
import com.example.tabletop.auth.dto.LoginResponseDTO;
import com.example.tabletop.auth.exception.InvalidPasswordException;
import com.example.tabletop.auth.exception.LogoutException;
import com.example.tabletop.auth.exception.TokenException;
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
	private final PasswordEncoder passwordEncoder;
	private final SellerRepository sellerRepository;

	@Transactional
	public LoginResponseDTO login(LoginRequestDTO loginRequest) throws SellerNotFoundException, InvalidPasswordException{
		log.info("로그인 시도: {}", loginRequest.getLoginId());
		Seller seller = sellerRepository.findByLoginId(loginRequest.getLoginId())
				.orElseThrow(() -> new SellerNotFoundException("해당 로그인 ID가 존재하지 않습니다."));

		if (!passwordEncoder.matches(loginRequest.getPassword(), seller.getPassword())) {
			throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
		}

		String accessToken = jwtTokenProvider.createAccessToken(
				new UsernamePasswordAuthenticationToken(new CustomUserDetails(seller), seller.getPassword()));

		String refreshToken = jwtTokenProvider.createRefreshToken(
				new UsernamePasswordAuthenticationToken(new CustomUserDetails(seller), seller.getPassword()));

		seller.setRefreshToken(refreshToken);
		sellerRepository.save(seller);

		return new LoginResponseDTO(seller.getLoginId(), "Bearer", accessToken, refreshToken);
	}

	@Transactional
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

	@Transactional(readOnly = true)
	public String refreshAccessToken(String refreshToken) throws TokenException, SellerNotFoundException {
		log.info("AccessToken 갱신 시도: {}", refreshToken);

		if (!jwtTokenProvider.validateToken(refreshToken)) {
			log.error("RefreshToken 만료 되었습니다.");
			throw new TokenException("리플래시 토큰이 만료되었습니다. 다시 로그인해 주세요.");			
		}
		
		Seller seller = sellerRepository.findByRefreshToken(refreshToken)
				.orElseThrow(() -> new SellerNotFoundException("해당 RefreshToken 으로 판매자를 찾을 수 없습니다."));

		String newAccessToken = jwtTokenProvider.createAccessToken(
				new UsernamePasswordAuthenticationToken(new CustomUserDetails(seller), seller.getPassword()));
		
		return newAccessToken;				
	}
}