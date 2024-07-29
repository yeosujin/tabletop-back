package com.example.tabletop.orderitem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemRequestDto {
    private Long menuId;
    private Integer quantity;
    private Integer price;
}
