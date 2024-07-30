package com.example.tabletop.seller.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.tabletop.commons.sse.service.SseService;
import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.repository.MenuRepository;
import com.example.tabletop.order.dto.CreateOrderRequest;
import com.example.tabletop.order.dto.OrderResponseDto;
import com.example.tabletop.order.entity.Order;
import com.example.tabletop.order.repository.OrderRepository;
import com.example.tabletop.order.service.OrderService;
import com.example.tabletop.orderitem.dto.OrderItemRequestDto;
import com.example.tabletop.orderitem.entity.Orderitem;
import com.example.tabletop.orderitem.repository.OrderitemRepository;
import com.example.tabletop.payment.dto.PaymentRequestDto;
import com.example.tabletop.payment.entity.Payment;
import com.example.tabletop.payment.enums.PaymentMethod;
import com.example.tabletop.payment.repository.PaymentRepository;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.repository.StoreRepository;

import jakarta.persistence.EntityNotFoundException;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderitemRepository orderItemRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SseService sseService;

    @InjectMocks
    private OrderService orderService;

    private CreateOrderRequest createOrderRequest;
    private Store store;
    private Menu menu;
    private Order order;
    private Orderitem orderItem;
    private Payment payment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        store = new Store();
        store.setStoreId(1L);

        menu = new Menu();
        menu.setId(1L);
        menu.setPrice(1000);

        order = new Order();
        order.setOrderId(1L);
        order.setStore(store);
        order.setTableNumber(10);
        order.setTotalPrice(2000);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderItem = new Orderitem();
        orderItem.setOrder(order);
        orderItem.setMenu(menu);
        orderItem.setQuantity(2);
        orderItem.setPrice(1000);

        payment = new Payment();
        payment.setId(1L);
        payment.setPaymentMethod(PaymentMethod.CARD);
        payment.setOrder(order);
        payment.setAmount(BigDecimal.valueOf(2000));
        payment.setTransactionId("12345");

        OrderItemRequestDto orderItemRequestDto = new OrderItemRequestDto();
        orderItemRequestDto.setMenuId(1L);
        orderItemRequestDto.setQuantity(2);
        orderItemRequestDto.setPrice(1000);

        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setPaymentMethod("CARD");
        paymentRequestDto.setTransactionId("12345");

        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setStoreId(1L);
        createOrderRequest.setTableNumber(10);
        createOrderRequest.setOrderItems(List.of(orderItemRequestDto));
        createOrderRequest.setPayment(paymentRequestDto);
    }

    @Test
    void testCreateOrder() {
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.save(any(Orderitem.class))).thenReturn(orderItem);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        OrderResponseDto orderResponseDto = orderService.createOrder(createOrderRequest);

        assertNotNull(orderResponseDto);
        assertEquals(1L, orderResponseDto.getOrderId());
        assertEquals(2000, orderResponseDto.getTotalPrice());
        assertEquals("CARD", orderResponseDto.getPayment().getPaymentMethod());
    }

    @Test
    void testCreateOrderStoreNotFound() {
        when(storeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(createOrderRequest));
    }

    @Test
    void testCreateOrderMenuNotFound() {
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(menuRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(createOrderRequest));
    }
}