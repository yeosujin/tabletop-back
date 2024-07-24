package com.example.tabletop.menu.controller;

import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @GetMapping("/stores/{storeId}/menu")
    public ResponseEntity<Menu> getStoreMenu(@PathVariable Long storeId) {
        Menu menu = menuService.getMenuByStoreId(storeId);
        return ResponseEntity.ok(menu);
    }
}
