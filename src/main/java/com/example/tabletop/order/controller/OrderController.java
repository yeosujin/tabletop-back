package com.example.tabletop.order.controller;

import com.example.tabletop.order.dto.CreateOrderRequest;
import com.example.tabletop.order.entity.Order;
import com.example.tabletop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/")
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        Order createdOrder = orderService.createOrder(
                request.getStoreId(),
                request.getTableNumber(),
                request.getTotalPrice()
        );
        return ResponseEntity.ok(createdOrder);
    }
}
