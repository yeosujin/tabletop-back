package com.example.tabletop.payment.dto;

import lombok.Data;

@Data
public class PaymentRequestDto {
    private Long paymentMethodId;
    private String transactionId;
}
