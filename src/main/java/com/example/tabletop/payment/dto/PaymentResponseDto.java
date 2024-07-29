package com.example.tabletop.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentResponseDto {
    private Long paymentId;
    private String paymentMethod;
    private BigDecimal amount;
    private String transactionId;
}
