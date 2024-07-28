package com.example.tabletop.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tabletop.auth.dto.PasswordResetRequestDTO;
import com.example.tabletop.auth.exception.CertificationGenerationException;
import com.example.tabletop.auth.exception.CustomMessagingException;
import com.example.tabletop.auth.service.MailService;
import com.example.tabletop.seller.exception.SellerNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/mail")
@RestController
public class MailController {

	private final MailService mailService;

	// 비밀번호 찾기
	@PostMapping("/temporaryPwd")
	public ResponseEntity<String> sendTemporaryPassword(@RequestBody PasswordResetRequestDTO passwordResetRequestDTO) throws CertificationGenerationException, SellerNotFoundException, CustomMessagingException {
		mailService.resetPassword(passwordResetRequestDTO);
		return new ResponseEntity<>("임시 비밀번호가 이메일로 전송되었습니다.", HttpStatus.OK);
	}

	// 회원가입 : 이메일 인증(리액트에서 인증번호 일치/불일치 검증(전달한 인증번호와 입력창에 인증번호와 비교)
	@PostMapping("/sendVerificationCode")
	public ResponseEntity<String> sendVerificationCode(@RequestParam String email) throws CertificationGenerationException, CustomMessagingException {
		String verificationCode = mailService.sendVerificationCode(email);
		return new ResponseEntity<>(verificationCode, HttpStatus.OK);
	}
}