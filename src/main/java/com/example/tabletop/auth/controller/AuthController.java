package com.example.tabletop.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tabletop.auth.dto.LoginRequestDTO;
import com.example.tabletop.auth.dto.LoginResponseDTO;
import com.example.tabletop.auth.exception.InvalidPasswordException;
import com.example.tabletop.auth.exception.LogoutException;
import com.example.tabletop.auth.exception.TokenException;
import com.example.tabletop.auth.service.AuthService;
import com.example.tabletop.seller.exception.SellerNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {
	
	private final AuthService authService;
	
	// 로그인
	@PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) throws SellerNotFoundException, InvalidPasswordException {
        LoginResponseDTO loginResponse = authService.login(loginRequest);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
	
	// 로그아웃
	@PostMapping("/{loginId}")
    public ResponseEntity<String> logout(@PathVariable String loginId) throws SellerNotFoundException, LogoutException {
		authService.logout(loginId);
        return new ResponseEntity<>("로그아웃 되었습니다.", HttpStatus.OK);
    }
	
	// 리플래시 토큰으로 엑세스 토큰 재발급
	@PostMapping("/token/refresh")
    public ResponseEntity<String> refreshToken(@RequestHeader("REFRESH_TOKEN") String refreshToken) throws TokenException, SellerNotFoundException {
        String newAccessToken = authService.refreshAccessToken(refreshToken);  
        return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);
    }	
}