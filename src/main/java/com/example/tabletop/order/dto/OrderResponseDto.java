package com.example.tabletop.order.dto;

import com.example.tabletop.orderitem.dto.OrderItemRequestDto;
import com.example.tabletop.orderitem.dto.OrderItemResponseDto;
import com.example.tabletop.payment.dto.PaymentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private Integer waitingNumber;
    private Integer totalPrice;
    private List<OrderItemResponseDto> orderItems;
    private LocalDateTime createdAt;
}
