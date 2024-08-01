//package com.example.tabletop.order.controller;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import com.example.tabletop.order.controller.OrderController;
//import com.example.tabletop.order.dto.CreateOrderRequest;
//import com.example.tabletop.order.dto.OrderResponseDto;
//import com.example.tabletop.order.service.OrderService;
//import com.example.tabletop.orderitem.dto.OrderItemRequestDto;
//import com.example.tabletop.payment.dto.PaymentRequestDto;
//import com.example.tabletop.payment.dto.PaymentResponseDto;
//
//@SpringBootTest
//public class OrderControllerTest {
//
//	@Mock
//    private OrderService orderService;
//
//    @InjectMocks
//    private OrderController orderController;
//
//    private CreateOrderRequest createOrderRequest;
//    private OrderResponseDto orderResponseDto;
//	
//    @BeforeEach
//    void setUp() {
//        OrderItemRequestDto orderItemRequestDto = new OrderItemRequestDto();
//        orderItemRequestDto.setMenuId(1L);
//        orderItemRequestDto.setQuantity(2);
//        orderItemRequestDto.setPrice(1000);
//
//        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
//        paymentRequestDto.setPaymentMethod("CARD");
//        paymentRequestDto.setTransactionId("12345");
//
//        createOrderRequest = new CreateOrderRequest();
//        createOrderRequest.setStoreId(1L);
//        createOrderRequest.setTableNumber(10);
//        createOrderRequest.setOrderItems(List.of(orderItemRequestDto));
//        createOrderRequest.setPayment(paymentRequestDto);
//
//        orderResponseDto = new OrderResponseDto(1L, 1, 2000, LocalDateTime.now(), new PaymentResponseDto(1L, "CARD", BigDecimal.valueOf(2000), "12345"));
//    }
//
//    @Test
//    void testCreateOrder() {
//        when(orderService.createOrder(createOrderRequest)).thenReturn(orderResponseDto);
//
//        ResponseEntity<OrderResponseDto> response = orderController.createOrder(createOrderRequest);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals(orderResponseDto.getOrderId(), response.getBody().getOrderId());
//    }
//}