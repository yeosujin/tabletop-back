package com.example.tabletop.sales.repository;

import com.example.tabletop.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesRepository extends JpaRepository<Order, Long> {
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.createdAt BETWEEN :start AND :end AND o.store.id = :storeId")
    Integer getSalesBetweenDates(LocalDateTime start, LocalDateTime end, Long storeId);

    @Query("SELECT oi.menu.name, SUM(oi.quantity), SUM(oi.price * oi.quantity) " +
           "FROM Orderitem oi WHERE oi.order.store.id = :storeId " +
           "GROUP BY oi.menu.name ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> getMenuSales(Long storeId);

    @Query("SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m') as month, " +
           "SUM(o.totalPrice), COUNT(DISTINCT o.orderId) " +
           "FROM Order o WHERE o.store.id = :storeId " +
           "GROUP BY month ORDER BY month DESC")
    List<Object[]> getMonthlySales(Long storeId);

    @Query("SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%u') as week, " +
           "SUM(o.totalPrice), COUNT(DISTINCT o.orderId) " +
           "FROM Order o WHERE o.store.id = :storeId " +
           "GROUP BY week ORDER BY week DESC")
    List<Object[]> getWeeklySales(Long storeId);

    @Query("SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m-%d') as day, " +
           "SUM(o.totalPrice), COUNT(DISTINCT o.orderId) " +
           "FROM Order o WHERE o.createdAt >= :startOfWeek AND o.store.id = :storeId " +
           "GROUP BY day ORDER BY day DESC")
    List<Object[]> getWeeklyDailySales(LocalDateTime startOfWeek, Long storeId);
}