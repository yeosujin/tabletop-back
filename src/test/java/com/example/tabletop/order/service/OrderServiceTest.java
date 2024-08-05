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
import com.example.tabletop.payment.entity.Payment;
import com.example.tabletop.payment.enums.PaymentMethod;
import com.example.tabletop.payment.repository.PaymentRepository;
import com.example.tabletop.seller.entity.Seller;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.enums.StoreType;
import com.example.tabletop.store.repository.StoreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        // given
        Long storeId = 1L;
        CreateOrderRequest orderRequest = new CreateOrderRequest();
        orderRequest.setStoreId(storeId);
        orderRequest.setTableNumber(1);
        orderRequest.setOrderItems(List.of(new OrderItemRequestDto(1L, 2, 1000)));

        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setPaymentMethod("CARD");
        paymentRequestDto.setTransactionId("txn123");
        orderRequest.setPayment(paymentRequestDto);

        Seller seller = new Seller();
        Store store = Store.builder()
                .storeId(storeId)
                .name("Test Store")
                .storeType(StoreType.ORDINARY)
                .corporateRegistrationNumber("123456789")
                .openDate(LocalDate.now())
                .closeDate(LocalDate.now().plusYears(1))
                .description("Test Description")
                .address("Test Address")
                .notice("Test Notice")
                .openTime(LocalTime.of(9, 0))
                .closeTime(LocalTime.of(22, 0))
                .holidays(new HashSet<>())
                .seller(seller)
                .build();

        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("Test Menu");

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));
        when(orderRepository.countTodayOrdersByStoreId(storeId)).thenReturn(0);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order savedOrder = (Order) i.getArguments()[0];
            savedOrder.setOrderId(1L);
            return savedOrder;
        });
        when(orderItemRepository.save(any(Orderitem.class))).thenAnswer(i -> i.getArguments()[0]);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);

        // when
        OrderResponseDto responseDto = orderService.createOrder(orderRequest);

        // then
        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(2000, responseDto.getTotalPrice());
        Assertions.assertEquals(1, responseDto.getWaitingNumber());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).save(any(Orderitem.class));
        verify(paymentRepository, times(1)).save(argThat(payment -> {
            Assertions.assertEquals(PaymentMethod.CARD, payment.getPaymentMethod());
            Assertions.assertEquals(BigDecimal.valueOf(2000), payment.getAmount());
            Assertions.assertEquals("txn123", payment.getTransactionId());
            Assertions.assertFalse(payment.getIsRefunded());
            Assertions.assertNotNull(payment.getCreatedAt());
            Assertions.assertNotNull(payment.getUpdatedAt());
            return true;
        }));
    }

    @Test
    void testReadKitchenOrders() {
        // given
        Long storeId = 1L;
        LocalDateTime orderTime = LocalDateTime.now();
        Order order = new Order();
        order.setOrderId(1L);
        order.setWaitingNumber(1);
        order.setTotalPrice(2000);
        order.setCreatedAt(orderTime);
        order.setStatus(0);

        Orderitem orderItem = new Orderitem();
        Menu menu = new Menu();
        menu.setName("Test Menu");
        orderItem.setMenu(menu);
        orderItem.setQuantity(2);
        orderItem.setPrice(1000);

        Payment payment = new Payment();
        payment.setTransactionId("imp_123456789");

        when(orderRepository.findByStore_StoreId(storeId)).thenReturn(List.of(order));
        when(orderItemRepository.findByOrder_OrderId(1L)).thenReturn(List.of(orderItem));
        when(paymentRepository.findByOrder_OrderId(1L)).thenReturn(payment);

        // when
        List<KitchenOrderResponseDto> responseList = orderService.readKitchenOrders(storeId);

        // then
        Assertions.assertFalse(responseList.isEmpty());
        KitchenOrderResponseDto response = responseList.get(0);
        Assertions.assertEquals(1L, response.getOrderId());
        Assertions.assertEquals(1, response.getWaitingNumber());
        Assertions.assertEquals(2000, response.getTotalPrice());
        Assertions.assertEquals(orderTime, response.getCreatedAt());
        Assertions.assertEquals(0, response.getStatus());
        Assertions.assertEquals("imp_123456789", response.getImp_uid());
        
        Assertions.assertFalse(response.getOrderItems().isEmpty());
        KitchenOrderResponseDto.KitchenOrderItemDto itemResponse = response.getOrderItems().get(0);
        Assertions.assertEquals("Test Menu", itemResponse.getMenuName());
        Assertions.assertEquals(2, itemResponse.getQuantity());
        Assertions.assertEquals(1000, itemResponse.getPrice());
    }

    @Test
    void testUpdateOrderStatus() {
        // given
        Long orderId = 1L;
        Integer newStatus = 1;
        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(0);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        // when
        Order updatedOrder = orderService.updateOrderStatus(orderId, newStatus);

        // then
        Assertions.assertNotNull(updatedOrder);
        Assertions.assertEquals(newStatus, updatedOrder.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}