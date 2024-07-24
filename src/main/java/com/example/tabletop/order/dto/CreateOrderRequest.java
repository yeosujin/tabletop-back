package com.example.tabletop.order.dto;

import com.example.tabletop.orderitem.dto.OrderItemRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private Long storeId;
    private Integer tableNumber;
    private List<OrderItemRequestDto> orderItems;
}