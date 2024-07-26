package com.example.tabletop.order.dto;

import com.example.tabletop.orderitem.dto.OrderItemRequestDto;
import com.example.tabletop.payment.dto.PaymentRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private Long storeId;
    private Integer tableNumber;
    private List<OrderItemRequestDto> orderItems;
    private PaymentRequestDto payment;
}