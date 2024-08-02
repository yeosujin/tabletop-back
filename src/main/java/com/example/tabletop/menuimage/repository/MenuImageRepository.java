package com.example.tabletop.menuimage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tabletop.menuimage.entity.MenuImage;

@Repository
public interface MenuImageRepository extends JpaRepository<MenuImage, Long> {

    List<MenuImage> findByMenuId(Long menuId);

}
