package com.example.tabletop.order.controller;

import com.example.tabletop.order.dto.CreateOrderRequest;
import com.example.tabletop.order.dto.KitchenOrderResponseDto;
import com.example.tabletop.order.dto.OrderResponseDto;
import com.example.tabletop.order.entity.Order;
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
        return ResponseEntity.ok(orderService.readKitchenOrders(storeId));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long orderId) {
        Order canceledOrder = orderService.updateOrderStatus(orderId, 2);
        return ResponseEntity.ok(canceledOrder);
    }

    @PutMapping("/{orderId}/complete")
    public ResponseEntity<Order> completeOrder(@PathVariable Long orderId) {
        Order completedOrder = orderService.updateOrderStatus(orderId, 1);
        return ResponseEntity.ok(completedOrder);
    }

    @PostMapping("/complete")
    public ResponseEntity<OrderResponseDto> completeOrderFromMobile(
            @RequestParam Long storeId,
            @RequestParam String impUid,
            @RequestParam String merchantUid) {
        OrderResponseDto orderResponseDto = orderService.createOrderFromPayment(storeId, impUid, merchantUid);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDto);
    }

    @GetMapping("/payment")
    public ResponseEntity<OrderResponseDto> getOrderByPayment(
            @RequestParam Long storeId,
            @RequestParam String impUid,
            @RequestParam String merchantUid) {
        OrderResponseDto orderResponseDto = orderService.getOrderByPayment(storeId, impUid, merchantUid);
        return ResponseEntity.ok(orderResponseDto);
    }
}