package com.example.tabletop.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class KitchenOrderResponseDto {
    private Long orderId;
    private Integer waitingNumber;
    private Integer totalPrice;
    private List<KitchenOrderItemDto> orderItems;
    private LocalDateTime createdAt;
    private int status;

    @Data
    @AllArgsConstructor
    public static class KitchenOrderItemDto {
        private String menuName;
        private Integer quantity;
        private Integer price;
    }
}