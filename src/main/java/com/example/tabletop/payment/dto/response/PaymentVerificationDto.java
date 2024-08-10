package com.example.tabletop.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationDto {
    private String impUid;
    private String merchantUid;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
    private LocalDateTime paidAt;

    // 결제 성공 여부를 확인하는 메서드
    public boolean isSuccessful() {
        return "paid".equals(status);
    }
}