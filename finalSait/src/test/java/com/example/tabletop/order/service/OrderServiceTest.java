package com.example.tabletop.order.service;

import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.repository.MenuRepository;
import com.example.tabletop.order.dto.CreateOrderRequest;
import com.example.tabletop.order.dto.KitchenOrderResponseDto;
import com.example.tabletop.order.dto.OrderResponseDto;
import com.example.tabletop.order.entity.Order;
import com.example.tabletop.order.repository.OrderRepository;
import com.example.tabletop.orderitem.dto.OrderItemRequestDto;
import com.example.tabletop.orderitem.entity.Orderitem;
import com.example.tabletop.orderitem.repository.OrderitemRepository;
import com.example.tabletop.payment.dto.PaymentRequestDto;
import com.example.tabletop.payment.repository.PaymentRepository;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setStoreId(1L);
        request.setTableNumber(1);

        OrderItemRequestDto orderItemRequestDto = new OrderItemRequestDto();
        orderItemRequestDto.setMenuId(1L);
        orderItemRequestDto.setQuantity(2);
        orderItemRequestDto.setPrice(1000);
        request.setOrderItems(Arrays.asList(orderItemRequestDto));

        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setPaymentMethod("CARD");
        paymentRequestDto.setTransactionId("123456");
        request.setPayment(paymentRequestDto);

        Store store = new Store();
        store.setStoreId(1L);

        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("Test Menu");

        Order savedOrder = new Order();
        savedOrder.setOrderId(1L);
        savedOrder.setWaitingNumber(1);
        savedOrder.setTotalPrice(2000);
        savedOrder.setCreatedAt(LocalDateTime.now());
        savedOrder.setStatus(0);

        Orderitem savedOrderItem = new Orderitem();
        savedOrderItem.setOrderItemId(1L);
        savedOrderItem.setQuantity(2);
        savedOrderItem.setPrice(1000);
        savedOrderItem.setMenu(menu);
        savedOrderItem.setOrder(savedOrder);

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(orderRepository.countTodayOrdersByStoreId(1L)).thenReturn(0);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderItemRepository.save(any(Orderitem.class))).thenReturn(savedOrderItem);

        // Act
        OrderResponseDto result = orderService.createOrder(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals(1, result.getWaitingNumber());
        assertEquals(2000, result.getTotalPrice());
        assertEquals(0, result.getStatus());
        assertEquals(1, result.getOrderItems().size());
        assertEquals("Test Menu", result.getOrderItems().get(0).getMenuName());

        verify(storeRepository).findById(1L);
        verify(menuRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).save(any(Orderitem.class));
        verify(paymentRepository).save(any());
    }

    @Test
    void readKitchenOrders() {
        // Arrange
        Long storeId = 1L;
        Order order = new Order();
        order.setOrderId(1L);
        order.setWaitingNumber(1);
        order.setTotalPrice(2000);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(0);

        Orderitem orderitem = new Orderitem();
        Menu menu = new Menu();
        menu.setName("Test Menu");
        orderitem.setMenu(menu);
        orderitem.setQuantity(2);
        orderitem.setPrice(1000);

        when(orderRepository.findByStore_StoreId(storeId)).thenReturn(Arrays.asList(order));
        when(orderItemRepository.findByOrder_OrderId(1L)).thenReturn(Arrays.asList(orderitem));

        // Act
        List<KitchenOrderResponseDto> result = orderService.readKitchenOrders(storeId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getOrderId());
        assertEquals(1, result.get(0).getWaitingNumber());
        assertEquals(2000, result.get(0).getTotalPrice());
        assertEquals(0, result.get(0).getStatus());
        assertEquals(1, result.get(0).getOrderItems().size());

        verify(orderRepository).findByStore_StoreId(storeId);
        verify(orderItemRepository).findByOrder_OrderId(1L);
    }

    @Test
    void updateOrderStatus() {
        // Arrange
        Long orderId = 1L;
        Integer newStatus = 2;

        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(0);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order result = orderService.updateOrderStatus(orderId, newStatus);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertEquals(newStatus, result.getStatus());

        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(order);
    }
}