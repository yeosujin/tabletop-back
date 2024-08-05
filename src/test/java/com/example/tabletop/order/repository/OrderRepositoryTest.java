package com.example.tabletop.order.repository;

import com.example.tabletop.order.entity.Order;
import com.example.tabletop.seller.entity.Seller;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.enums.StoreType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testCRUD() {
        // Create a Seller first
        Seller seller = new Seller();
        seller = entityManager.persist(seller);

        // Create a Store
        Store store = Store.builder()
                .name("Test Store")
                .storeType(StoreType.ORDINARY)
                .corporateRegistrationNumber("123456789")
                .openDate(LocalDate.now())
                .closeDate(LocalDate.now().plusYears(1))
                .description("Test description")
                .address("Test address")
                .openTime(LocalTime.of(9, 0))
                .closeTime(LocalTime.of(22, 0))
                .holidays(new HashSet<>())
                .seller(seller)
                .build();
        store = entityManager.persist(store);

        // Create
        Order order = new Order();
        order.setStore(store);
        order.setTableNumber(1);
        order.setTotalPrice(100);
        order.setWaitingNumber(1);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStatus(1);

        Order savedOrder = orderRepository.save(order);
        Assertions.assertNotNull(savedOrder);

        // Read
        List<Order> orders = orderRepository.findByStore_StoreId(store.getStoreId());
        Assertions.assertNotNull(orders);
        Assertions.assertEquals(1, orders.size());
        Assertions.assertEquals(order.getTotalPrice(), orders.get(0).getTotalPrice());

        // Update
        savedOrder.setTotalPrice(150);
        Order updatedOrder = orderRepository.save(savedOrder);
        Assertions.assertEquals(150, updatedOrder.getTotalPrice());

        // Delete
        orderRepository.deleteById(savedOrder.getOrderId());
        Assertions.assertTrue(orderRepository.findById(savedOrder.getOrderId()).isEmpty());
    }

    @Test
    void testCountTodayOrdersByStoreId() {
        // Create a Seller
        Seller seller = new Seller();
        seller = entityManager.persist(seller);

        // Create a Store
        Store store = Store.builder()
                .name("Test Store")
                .storeType(StoreType.ORDINARY)
                .corporateRegistrationNumber("123456789")
                .openDate(LocalDate.now())
                .closeDate(LocalDate.now().plusYears(1))
                .description("Test description")
                .address("Test address")
                .openTime(LocalTime.of(9, 0))
                .closeTime(LocalTime.of(22, 0))
                .holidays(new HashSet<>())
                .seller(seller)
                .build();
        store = entityManager.persist(store);

        // Create orders for today
        for (int i = 0; i < 3; i++) {
            Order order = new Order();
            order.setStore(store);
            order.setTableNumber(i + 1);
            order.setTotalPrice(100 * (i + 1));
            order.setWaitingNumber(i + 1);
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            order.setStatus(1);
            entityManager.persist(order);
        }

        // Create an order for yesterday
        Order yesterdayOrder = new Order();
        yesterdayOrder.setStore(store);
        yesterdayOrder.setTableNumber(4);
        yesterdayOrder.setTotalPrice(500);
        yesterdayOrder.setWaitingNumber(4);
        yesterdayOrder.setCreatedAt(LocalDateTime.now().minusDays(1));
        yesterdayOrder.setUpdatedAt(LocalDateTime.now().minusDays(1));
        yesterdayOrder.setStatus(1);
        entityManager.persist(yesterdayOrder);

        entityManager.flush();

        // Test the count
        int todayOrdersCount = orderRepository.countTodayOrdersByStoreId(store.getStoreId());
        Assertions.assertEquals(3, todayOrdersCount);
    }
}