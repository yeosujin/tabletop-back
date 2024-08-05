package com.example.tabletop.order.repository;

import com.example.tabletop.order.entity.Order;
import com.example.tabletop.order.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testCRUD() {
        // Create
        Order order = new Order(null, 1L, LocalDateTime.now(), 100.0);
        Order savedOrder = orderRepository.save(order);
        Assertions.assertNotNull(savedOrder);

        // Read
        List<Order> orders = orderRepository.findByStoreId(1L);
        Assertions.assertNotNull(orders);
        Assertions.assertEquals(1, orders.size());
        Assertions.assertEquals(order.getTotalAmount(), orders.get(0).getTotalAmount());

        // Update
        savedOrder.setTotalAmount(150.0);
        Order updatedOrder = orderRepository.save(savedOrder);
        Assertions.assertEquals(150.0, updatedOrder.getTotalAmount());

        // Delete
        orderRepository.deleteById(savedOrder.getId());
        Assertions.assertTrue(orderRepository.findById(savedOrder.getId()).isEmpty());
    }
}