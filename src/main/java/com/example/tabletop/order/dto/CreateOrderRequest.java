package com.example.tabletop.order.dto;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private Long storeId;
    private Integer tableNumber;
    private Integer totalPrice;
}
