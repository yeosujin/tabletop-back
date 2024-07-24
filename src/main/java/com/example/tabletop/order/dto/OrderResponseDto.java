package com.example.tabletop.order.dto;

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

}
