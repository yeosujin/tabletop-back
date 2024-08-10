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
import com.example.tabletop.payment.dto.response.PaymentVerificationDto;
import com.example.tabletop.payment.entity.Payment;
import com.example.tabletop.payment.enums.PaymentMethod;
import com.example.tabletop.payment.repository.PaymentRepository;
import com.example.tabletop.payment.service.PaymentService;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Sinks;

import java.io.IOException;
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
    private final PaymentRepository paymentRepository;
    private final OrderitemRepository orderItemRepository;
    private final PaymentService paymentService;
    private final SseService sseService;

    @Transactional
    public OrderResponseDto createOrder(CreateOrderRequest orderRequestDto) {
        Store store = storeRepository.findById(orderRequestDto.getStoreId())
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));

        Order order = new Order();
        order.setStore(store);
        order.setTableNumber(orderRequestDto.getTableNumber());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(0); // Assuming 0 is the initial status

        BigDecimal totalPrice = calculateTotalPrice(orderRequestDto.getOrderItems());
        order.setTotalPrice(totalPrice.intValue());

        int waitingNumber = orderRepository.countTodayOrdersByStoreId(store.getStoreId()) + 1;
        order.setWaitingNumber(waitingNumber);

        Order savedOrder = orderRepository.save(order);

        List<Orderitem> orderItems = createOrderItems(savedOrder, orderRequestDto.getOrderItems());

        // Verify payment
        PaymentVerificationDto verificationDto = paymentService.verifyPayment(orderRequestDto.getPayment().getTransactionId(), totalPrice);
        paymentService.createPayment(verificationDto, savedOrder);

        // Notify kitchen
        KitchenOrderResponseDto kitchenOrderResponseDto = createKitchenOrderResponseDto(savedOrder, orderItems);
        sseService.notifyNewOrder(store.getStoreId(), kitchenOrderResponseDto).subscribe();

        return createOrderResponseDto(savedOrder, orderItems);
    }

    private BigDecimal calculateTotalPrice(List<OrderItemRequestDto> orderItems) {
        return orderItems.stream()
                .map(item -> BigDecimal.valueOf(item.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
                order.getStatus());
    }

    private KitchenOrderResponseDto createKitchenOrderResponseDto(Order order, List<Orderitem> orderItems) {
        return new KitchenOrderResponseDto(
                order.getOrderId(),
                order.getWaitingNumber(),
                order.getTotalPrice(),
                orderItems.stream()
                        .map(item -> new KitchenOrderResponseDto.KitchenOrderItemDto(
                                item.getMenu().getName(), item.getQuantity(), item.getPrice()))
                        .toList(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getPayment().getTransactionId());
    }

    public List<KitchenOrderResponseDto> readKitchenOrders(Long storeId) {
        List<Order> orders = orderRepository.findByStore_StoreId(storeId);

        return orders.stream().map(order -> {
            List<Orderitem> orderitems = orderItemRepository.findByOrder_OrderId(order.getOrderId());
            Payment payment = paymentRepository.findByOrder_OrderId(order.getOrderId());
            return new KitchenOrderResponseDto(order.getOrderId(), order.getWaitingNumber(), order.getTotalPrice(),
                    orderitems.stream().map(orderitem -> new KitchenOrderResponseDto.KitchenOrderItemDto(orderitem.getMenu() != null ? orderitem.getMenu().getName() : "Unknown Menu", orderitem.getQuantity(), orderitem.getPrice())).toList(),
                    order.getCreatedAt(), order.getStatus(), payment.getTransactionId());
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
}
