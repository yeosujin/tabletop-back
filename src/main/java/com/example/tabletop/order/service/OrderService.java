package com.example.tabletop.order.service;

import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.repository.MenuRepository;
import com.example.tabletop.order.dto.CreateOrderRequest;
import com.example.tabletop.order.dto.OrderResponseDto;
import com.example.tabletop.order.entity.Order;
import com.example.tabletop.order.repository.OrderRepository;
import com.example.tabletop.orderitem.dto.OrderItemRequestDto;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final OrderitemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;

    public OrderResponseDto createOrder(CreateOrderRequest orderRequestDto) {
        Store store = storeRepository.findById(orderRequestDto.getStoreId())
                .orElseThrow(() -> new EntityNotFoundException("Store not found"));

        Order order = new Order();
        order.setStore(store);
        order.setTableNumber(orderRequestDto.getTableNumber());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(0); // Assuming 0 is the initial status

        int totalPrice = 0;
        for (OrderItemRequestDto itemDto : orderRequestDto.getOrderItems()) {
            totalPrice += itemDto.getPrice() * itemDto.getQuantity();
        }
        order.setTotalPrice(totalPrice);

        int waitingNumber = orderRepository.countTodayOrdersByStoreId(store.getStoreId()) + 1;
        order.setWaitingNumber(waitingNumber);

        order = orderRepository.save(order);

        for (OrderItemRequestDto itemDto : orderRequestDto.getOrderItems()) {
            Menu menu = menuRepository.findById(itemDto.getMenuId())
                    .orElseThrow(() -> new EntityNotFoundException("Menu not found"));

            Orderitem orderItem = new Orderitem();
            orderItem.setOrder(order);
            orderItem.setMenu(menu);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(itemDto.getPrice());

            orderItemRepository.save(orderItem);
        }

        // Create payment
        Payment payment = createPayment(orderRequestDto.getPayment(), order, BigDecimal.valueOf(totalPrice));

    return new OrderResponseDto(
        order.getOrderId(),
        order.getWaitingNumber(),
        order.getTotalPrice(),
        order.getCreatedAt(),
        new PaymentResponseDto(
            payment.getId(),
            payment.getPaymentMethod().name(),
            payment.getAmount(),
            payment.getTransactionId()));
    }

    private Payment createPayment(PaymentRequestDto paymentRequestDto, Order order, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setPaymentMethod(PaymentMethod.valueOf(paymentRequestDto.getPaymentMethod()));
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setIsRefunded(false);
        payment.setTransactionId(paymentRequestDto.getTransactionId());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }
}
