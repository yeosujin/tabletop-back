package com.example.tabletop.payment.service;

import com.example.tabletop.order.entity.Order;
import com.example.tabletop.payment.dto.response.PaymentVerificationDto;
import com.example.tabletop.payment.entity.Payment;
import com.example.tabletop.payment.enums.PaymentMethod;
import com.example.tabletop.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentVerificationDto verifyPayment(String impUid, BigDecimal amount) {
        // 테스트를 위한 모의 검증 로직
        log.info("Mocking payment verification for impUid: {} with amount: {}", impUid, amount);

        // 실제 환경에서는 이 부분에서 Portone API를 호출하여 결제를 검증해야 합니다.
        // 테스트 목적으로 항상 성공으로 처리합니다.
        return new PaymentVerificationDto(
                impUid,
                "merchant_" + System.currentTimeMillis(), // 임의의 merchant_uid 생성
                amount,
                "paid", // 항상 결제 성공으로 처리
                PaymentMethod.CARD.name(), // 기본값으로 카드 결제로 설정
                LocalDateTime.now()
        );
    }

    @Transactional
    public Payment createPayment(PaymentVerificationDto verificationDto, Order order) {
        Payment payment = new Payment();
        payment.setPaymentMethod(PaymentMethod.valueOf(verificationDto.getPaymentMethod().toUpperCase()));
        payment.setOrder(order);
        payment.setAmount(verificationDto.getAmount());
        payment.setIsRefunded(false);
        payment.setTransactionId(verificationDto.getImpUid());
        payment.setCreatedAt(verificationDto.getPaidAt());
        payment.setUpdatedAt(verificationDto.getPaidAt());

        return paymentRepository.save(payment);
    }
}