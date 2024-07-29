package com.example.tabletop.order.dto;

import com.example.tabletop.payment.dto.PaymentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private Integer waitingNumber;
    private Integer totalPrice;
    private LocalDateTime createdAt;
    private PaymentResponseDto payment;
}
