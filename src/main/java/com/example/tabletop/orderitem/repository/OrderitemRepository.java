package com.example.tabletop.orderitem.repository;

import com.example.tabletop.orderitem.entity.Orderitem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderitemRepository extends JpaRepository<Orderitem, Long> {
	@Modifying
	@Query("UPDATE Orderitem o SET o.menu = NULL WHERE o.menu.id = :menuId")
	void nullifyMenuReference(@Param("menuId") Long menuId);
}
