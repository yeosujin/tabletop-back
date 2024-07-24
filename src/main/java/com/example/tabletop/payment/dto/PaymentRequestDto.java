package com.example.tabletop.payment.dto;

import lombok.Data;

@Data
public class PaymentRequestDto {
    private String paymentMethod;
    private String transactionId;
}
