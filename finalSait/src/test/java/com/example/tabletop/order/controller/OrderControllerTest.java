package com.example.tabletop.order.controller;

import com.example.tabletop.order.dto.CreateOrderRequest;
import com.example.tabletop.order.dto.KitchenOrderResponseDto;
import com.example.tabletop.order.dto.OrderResponseDto;
import com.example.tabletop.order.entity.Order;
import com.example.tabletop.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createOrder() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(); // 필요한 데이터 설정
        OrderResponseDto response = new OrderResponseDto(1L, 1, 100, Arrays.asList(), LocalDateTime.now(), 0);

        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1L));
    }

    @Test
    void getAllOrders() throws Exception {
        Long storeId = 1L;
        List<KitchenOrderResponseDto> responses = Arrays.asList(
                new KitchenOrderResponseDto(1L, 1, 100, Arrays.asList(), LocalDateTime.now(), 0)
        );

        when(orderService.readKitchenOrders(storeId)).thenReturn(responses);

        mockMvc.perform(get("/api/orders/{storeId}", storeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1L));
    }

    @Test
    void cancelOrder() throws Exception {
        Long orderId = 1L;
        Order canceledOrder = new Order(); // 필요한 데이터 설정
        canceledOrder.setOrderId(orderId);
        canceledOrder.setStatus(2);

        when(orderService.updateOrderStatus(orderId, 2)).thenReturn(canceledOrder);

        mockMvc.perform(put("/api/orders/{orderId}/cancel", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.status").value(2));
    }

    @Test
    void completeOrder() throws Exception {
        Long orderId = 1L;
        Order completedOrder = new Order(); // 필요한 데이터 설정
        completedOrder.setOrderId(orderId);
        completedOrder.setStatus(1);

        when(orderService.updateOrderStatus(orderId, 1)).thenReturn(completedOrder);

        mockMvc.perform(put("/api/orders/{orderId}/complete", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.status").value(1));
    }
}