package com.example.tabletop.menu.controller;

import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}/menus")
public class MenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ResponseEntity<List<Menu>> getMenus(
            @PathVariable Long storeId,
            @RequestParam(required = false) Long lastMenuId,
            @RequestParam(defaultValue = "20") int limit) {
        List<Menu> menus = menuService.getMenusForInfiniteScroll(storeId, lastMenuId, limit);
        return ResponseEntity.ok(menus);
    }

    // 나머지 메서드들은 그대로 유지...

    @PostMapping
    public ResponseEntity<Menu> createMenu(
            @PathVariable Long storeId,
            @RequestParam String name,
            @RequestParam Integer price,
            @RequestParam(required = false) String description,
            @RequestParam Boolean isAvailable,
            @RequestParam(required = false) MultipartFile image) {
        try {
            Menu newMenu = menuService.createMenu(storeId, name, price, description, isAvailable, image);
            return new ResponseEntity<>(newMenu, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{menuId}")
    public ResponseEntity<Menu> updateMenu(
            @PathVariable Long menuId,
            @RequestParam String name,
            @RequestParam Integer price,
            @RequestParam(required = false) String description,
            @RequestParam Boolean isAvailable,
            @RequestParam(required = false) MultipartFile image) {
        try {
            Menu updatedMenu = menuService.updateMenu(menuId, name, price, description, isAvailable, image);
            return ResponseEntity.ok(updatedMenu);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long menuId) {
        try {
            menuService.deleteMenu(menuId);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<Menu> getMenu(@PathVariable Long menuId) {
        Menu menu = menuService.getMenu(menuId);
        return ResponseEntity.ok(menu);
    }
}