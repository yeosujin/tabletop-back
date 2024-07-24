package com.example.tabletop.menu.repository;

import com.example.tabletop.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    Optional<Menu> findByStore_StoreId(Long storeId);
}
