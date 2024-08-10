package com.example.tabletop.order.service;

import com.example.tabletop.commons.sse.service.SseService;
import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.repository.MenuRepository;
import com.example.tabletop.order.dto.CreateOrderRequest;
import com.example.tabletop.order.dto.KitchenOrderResponseDto;
import com.example.tabletop.order.dto.OrderResponseDto;
import com.example.tabletop.order.entity.Order;
import com.example.tabletop.order.repository.OrderRepository;
import com.example.tabletop.orderitem.dto.OrderItemRequestDto;
import com.example.tabletop.orderitem.dto.OrderItemResponseDto;
import com.example.tabletop.orderitem.entity.Orderitem;
import com.example.tabletop.orderitem.repository.OrderitemRepository;
import com.example.tabletop.payment.dto.PaymentRequestDto;
import com.example.tabletop.payment.dto.PaymentResponseDto;
import com.example.tabletop.payment.entity.Payment;
import com.example.tabletop.payment.enums.PaymentMethod;
import com.example.tabletop.payment.repository.PaymentRepository;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Sinks;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final OrderitemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final SseService sseService;

    @Transactional
    public OrderResponseDto createOrder(CreateOrderRequest orderRequestDto) {
        Store store = storeRepository.findById(orderRequestDto.getStoreId())
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));

        Order order = createOrderEntity(store, orderRequestDto);
        Order savedOrder = orderRepository.save(order);

        List<Orderitem> orderItems = createOrderItems(savedOrder, orderRequestDto.getOrderItems());

        createPayment(orderRequestDto.getPayment(), savedOrder, BigDecimal.valueOf(savedOrder.getTotalPrice()));

        OrderResponseDto responseDto = createOrderResponseDto(savedOrder, orderItems);

        // Notify kitchen about the new order
        notifyKitchen(store.getStoreId(), savedOrder, orderItems);

        return responseDto;
    }

    @Transactional
    public OrderResponseDto createOrderFromPayment(Long storeId, String impUid, String merchantUid) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));

        // Here you should implement the logic to verify the payment with the payment provider
        // For this example, we'll assume the payment is valid

        Order order = new Order();
        order.setStore(store);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(0);

        int waitingNumber = orderRepository.countTodayOrdersByStoreId(store.getStoreId()) + 1;
        order.setWaitingNumber(waitingNumber);

        Order savedOrder = orderRepository.save(order);

        Payment payment = new Payment();
        payment.setPaymentMethod(PaymentMethod.CARD); // Assume card payment for mobile
        payment.setOrder(savedOrder);
        payment.setTransactionId(impUid);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        OrderResponseDto responseDto = new OrderResponseDto(
                savedOrder.getOrderId(),
                savedOrder.getWaitingNumber(),
                savedOrder.getTotalPrice(),
                new ArrayList<>(), // Empty list as we don't have order items
                savedOrder.getCreatedAt(),
                savedOrder.getStatus()
        );

        // Notify kitchen about the new order
        notifyKitchen(store.getStoreId(), savedOrder, new ArrayList<>());

        return responseDto;
    }

    public List<KitchenOrderResponseDto> readKitchenOrders(Long storeId) {
        List<Order> orders = orderRepository.findByStore_StoreId(storeId);

        return orders.stream().map(order -> {
            List<Orderitem> orderitems = orderItemRepository.findByOrder_OrderId(order.getOrderId());
            Payment payment = paymentRepository.findByOrder_OrderId(order.getOrderId());
            return createKitchenOrderResponseDto(order, orderitems, payment);
        }).toList();
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, Integer status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    private Order createOrderEntity(Store store, CreateOrderRequest orderRequestDto) {
        Order order = new Order();
        order.setStore(store);
        order.setTableNumber(orderRequestDto.getTableNumber());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(0);

        int totalPrice = orderRequestDto.getOrderItems().stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum();
        order.setTotalPrice(totalPrice);

        int waitingNumber = orderRepository.countTodayOrdersByStoreId(store.getStoreId()) + 1;
        order.setWaitingNumber(waitingNumber);

        return order;
    }

    private List<Orderitem> createOrderItems(Order order, List<OrderItemRequestDto> orderItemDtos) {
        List<Orderitem> orderItems = new ArrayList<>();

        for (OrderItemRequestDto itemDto : orderItemDtos) {
            Menu menu = menuRepository.findById(itemDto.getMenuId())
                    .orElseThrow(() -> new EntityNotFoundException("Menu not found"));

            Orderitem orderItem = new Orderitem();
            orderItem.setOrder(order);
            orderItem.setMenu(menu);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(itemDto.getPrice());

            orderItems.add(orderItemRepository.save(orderItem));
        }

        return orderItems;
    }

    private void createPayment(PaymentRequestDto paymentRequestDto, Order order, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setPaymentMethod(PaymentMethod.valueOf(paymentRequestDto.getPaymentMethod().toUpperCase()));
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setIsRefunded(false);
        payment.setTransactionId(paymentRequestDto.getTransactionId());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        paymentRepository.save(payment);
    }

    public OrderResponseDto getOrderByPayment(Long storeId, String impUid, String merchantUid) {
        Payment payment = paymentRepository.findByTransactionId(impUid)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for imp_uid: " + impUid));

        Order order = payment.getOrder();
        List<Orderitem> orderitems = orderItemRepository.findByOrder_OrderId(order.getOrderId());
        if (!order.getStore().getStoreId().equals(storeId)) {
            throw new IllegalArgumentException("Store ID does not match");
        }

        return createOrderResponseDto(order, orderitems);
    }

    private OrderResponseDto createOrderResponseDto(Order order, List<Orderitem> orderItems) {
        return new OrderResponseDto(
                order.getOrderId(),
                order.getWaitingNumber(),
                order.getTotalPrice(),
                orderItems.stream()
                        .map(item -> new OrderItemResponseDto(
                                item.getMenu().getName(), item.getQuantity(), item.getPrice()))
                        .toList(),
                order.getCreatedAt(),
                order.getStatus()
        );
    }

    private KitchenOrderResponseDto createKitchenOrderResponseDto(Order order, List<Orderitem> orderItems, Payment payment) {
        return new KitchenOrderResponseDto(
                order.getOrderId(),
                order.getWaitingNumber(),
                order.getTotalPrice(),
                orderItems.stream()
                        .map(item -> new KitchenOrderResponseDto.KitchenOrderItemDto(
                                item.getMenu() != null ? item.getMenu().getName() : "Unknown Menu",
                                item.getQuantity(),
                                item.getPrice()))
                        .toList(),
                order.getCreatedAt(),
                order.getStatus(),
                payment != null ? payment.getTransactionId() : null
        );
    }

    private void notifyKitchen(Long storeId, Order order, List<Orderitem> orderItems) {
        Payment payment = paymentRepository.findByOrder_OrderId(order.getOrderId());
        KitchenOrderResponseDto kitchenOrderResponseDto = createKitchenOrderResponseDto(order, orderItems, payment);
        sseService.notifyNewOrder(storeId, kitchenOrderResponseDto).subscribe();
    }
}