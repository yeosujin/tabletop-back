package com.example.tabletop.orderitem.repository;

import com.example.tabletop.orderitem.entity.Orderitem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderitemRepository extends JpaRepository<Orderitem, Long> {
    List<Orderitem> findByOrder_OrderId(Long orderid);
}
