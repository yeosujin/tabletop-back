package com.example.tabletop.order.controller;

import com.example.tabletop.order.dto.CreateOrderRequest;
import com.example.tabletop.order.dto.KitchenOrderResponseDto;
import com.example.tabletop.order.dto.OrderResponseDto;
import com.example.tabletop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody CreateOrderRequest orderRequestDto) {
        OrderResponseDto orderResponseDto = orderService.createOrder(orderRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDto);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<List<KitchenOrderResponseDto>> getAllOrders(@PathVariable Long storeId) {
        return ResponseEntity.ok(
          orderService.readKitchenOrders(storeId)
        );
    }

}
