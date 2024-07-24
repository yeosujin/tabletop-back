package com.example.tabletop.order.service;

import com.example.tabletop.order.entity.Order;
import com.example.tabletop.order.repository.OrderRepository;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public Order createOrder(Long storeId, Integer tableNumber, Integer totalPrice) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found with id: " + storeId));

        int todayOrderCount = orderRepository.countTodayOrdersByStoreId(storeId);
        int waitingNumber = todayOrderCount + 1;

        Order order = new Order();
        order.setStore(store);
        order.setTableNumber(tableNumber);
        order.setTotalPrice(totalPrice);
        order.setWaitingNumber(waitingNumber);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(0); // 0: 주문 접수, 다른 상태값은 필요에 따라 정의

        return orderRepository.save(order);
    }
}
