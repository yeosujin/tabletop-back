package com.example.tabletop.orderitem.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemResponseDto {
    private String menuName;
    private Integer quantity;
    private Integer price;
}
