package com.example.tabletop.orderitem.repository;

import com.example.tabletop.orderitem.entity.Orderitem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderitemRepository extends JpaRepository<Orderitem, Long> {

}
