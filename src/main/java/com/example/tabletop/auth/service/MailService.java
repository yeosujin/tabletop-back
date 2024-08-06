package com.example.tabletop.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabletop.auth.dto.PasswordResetRequestDTO;
import com.example.tabletop.auth.exception.CertificationGenerationException;
import com.example.tabletop.auth.exception.CustomMessagingException;
import com.example.tabletop.commons.util.CertificationGenerator;
import com.example.tabletop.seller.entity.Seller;
import com.example.tabletop.seller.exception.SellerNotFoundException;
import com.example.tabletop.seller.repository.SellerRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {

	@Value("${spring.mail.username}")
	private String from;

	private final JavaMailSender mailSender;
	private final CertificationGenerator generator;	
	private final PasswordEncoder passwordEncoder;
	private final SellerRepository sellerRepository;
	
	// 비밀번호 찾기
	@Transactional
	public void resetPassword(PasswordResetRequestDTO passwordResetRequestDTO) throws CertificationGenerationException, SellerNotFoundException, CustomMessagingException {
		log.info("비밀번호 재설정 요청: {}", passwordResetRequestDTO.getLoginId());
		Optional<Seller> seller = sellerRepository.findByLoginIdAndEmailAndMobile(
                passwordResetRequestDTO.getLoginId(), passwordResetRequestDTO.getEmail(),
                passwordResetRequestDTO.getMobile());

		if (seller.isPresent()) {
			String certificationNumber = generator.createCertificationNumber();
			String encodedPassword = passwordEncoder.encode(certificationNumber);

			Seller sellerEntity = seller.get();
			sellerEntity.setPassword(encodedPassword);
			sellerRepository.save(sellerEntity);

			String emailContent = String.format("임시비밀번호: %s <br><br> 로그인 후 비밀번호를 수정해주세요.", certificationNumber);
			sendMail(sellerEntity.getEmail(), emailContent);
		} else {
			log.error("해당 정보로 판매자를 찾을 수 없습니다: {}", passwordResetRequestDTO);
			throw new SellerNotFoundException("해당 정보로 판매자를 찾을 수 없습니다.");
		}
	}
	
	// 이메일 인증
	public String sendVerificationCode(String email) throws CertificationGenerationException, CustomMessagingException {
		log.info("이메일 인증 코드 요청: {}", email);
		String verificationCode = generator.createCertificationNumber();
        String emailContent = String.format("인증 코드: %s <br><br> 해당 코드를 회원가입 페이지에 입력해주세요.", verificationCode);
        sendMail(email, "[TableTop] 회원가입 이메일 인증 코드", emailContent);
        return verificationCode;
    }
	
	// 이메일 인증(메일 전송)
	private void sendMail(String email, String subject, String content) throws CustomMessagingException {
		try {
			log.info("인증 코드 이메일을 전송합니다");
	        MimeMessage mimeMessage = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
	        helper.setFrom(from);
	        helper.setTo(email);
	        helper.setSubject(subject);
	        helper.setText(content, true);
	        
	        mailSender.send(mimeMessage);
	        log.info("인증 코드 이메일 전송 완료: {}", email);
		} catch (MessagingException e) {
			log.error("인증 코드 이메일 전송 실패: {}", e.getMessage());
			throw new CustomMessagingException("인증 코드 이메일 전송 실패");
		}   
    }

	// 비밀번호 찾기(메일 전송)
	private void sendMail(String email, String content) throws CustomMessagingException {
		try {
			log.info("임시 비밀번호 이메일을 전송합니다");
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			helper.setFrom(from);
			helper.setTo(email);
			helper.setSubject("[TableTop] 임시 비밀번호 안내");
			helper.setText(content, true);
			
			mailSender.send(mimeMessage);
			log.info("임시 비밀번호 이메일 전송 완료: {}", email);
		} catch (MessagingException e) {
			log.error("임시 비밀번호 이메일 전송 실패: {}", e.getMessage());
			throw new CustomMessagingException("임시 비밀번호 이메일 전송 실패");
		}
	}	
}