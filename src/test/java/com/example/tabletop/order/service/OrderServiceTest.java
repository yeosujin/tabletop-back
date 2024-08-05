package com.example.tabletop.order.service;

import com.example.tabletop.order.entity.Order;
import com.example.tabletop.order.repository.OrderRepository;
import com.example.tabletop.order.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        // given
        Long storeId = 1L;
        LocalDateTime orderDateTime = LocalDateTime.now();
        double totalAmount = 100.0;

        Order order = new Order(null, storeId, orderDateTime, totalAmount);

        // when
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        Order savedOrder = orderService.createOrder(storeId, orderDateTime, totalAmount);

        // then
        Assertions.assertNotNull(savedOrder);
        Assertions.assertEquals(storeId, savedOrder.getStoreId());
        Assertions.assertEquals(orderDateTime, savedOrder.getOrderDateTime());
        Assertions.assertEquals(totalAmount, savedOrder.getTotalAmount());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testGetOrdersBy