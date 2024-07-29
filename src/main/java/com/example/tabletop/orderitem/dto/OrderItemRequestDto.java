package com.example.tabletop.orderitem.dto;

import lombok.Data;

@Data
public class OrderItemRequestDto {
    private Long menuId;
    private Integer quantity;
    private Integer price;
}
