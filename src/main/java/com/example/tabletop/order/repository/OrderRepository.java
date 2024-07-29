package com.example.tabletop.order.repository;

import com.example.tabletop.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT COUNT(o) FROM Order o WHERE o.store.storeId = :storeId AND DATE(o.createdAt) = CURRENT_DATE")
    int countTodayOrdersByStoreId(@Param("storeId") Long storeId);

    List<Order> findByStore_StoreId(Long storeId);
}
