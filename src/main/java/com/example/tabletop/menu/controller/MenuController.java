package com.example.tabletop.menu.controller;

import com.example.tabletop.menu.dto.MenuDTO;
import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class MenuController {
    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/api/stores/{storeId}/menus")
    public ResponseEntity<List<MenuDTO>> getMenus(
            @PathVariable Long storeId,
            @RequestParam(required = false) Long lastMenuId,
            @RequestParam(defaultValue = "20") int limit) {
        List<Menu> menus = menuService.getMenusForInfiniteScroll(storeId, lastMenuId, limit);
        List<MenuDTO> menuDTOs = menus.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(menuDTOs);
    }

    @PostMapping("/api/stores/{storeId}/menus")
    public ResponseEntity<MenuDTO> createMenu(
            @PathVariable Long storeId,
            @RequestBody MenuDTO menuDTO) {
    	System.out.println("Received MenuDTO: " + menuDTO);
        try {
            Menu newMenu = menuService.createMenu(
                storeId, 
                menuDTO.getName(), 
                menuDTO.getPrice(), 
                menuDTO.getDescription(), 
                menuDTO.getIsAvailable(), 
                menuDTO.getImage()
            );
            return new ResponseEntity<>(convertToDTO(newMenu), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/api/stores/{storeId}/menus/{menuId}")
    public ResponseEntity<MenuDTO> updateMenu(
            @PathVariable Long menuId,
            @ModelAttribute MenuDTO menuDTO) {
        try {
            Menu updatedMenu = menuService.updateMenu(
                menuId, 
                menuDTO.getName(), 
                menuDTO.getPrice(), 
                menuDTO.getDescription(), 
                menuDTO.getIsAvailable(), 
                menuDTO.getImage()
            );
            return ResponseEntity.ok(convertToDTO(updatedMenu));
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/api/stores/{storeId}/menus/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long menuId) {
        try {
            menuService.deleteMenu(menuId);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/stores/{storeId}/menus/{menuId}")
    public ResponseEntity<MenuDTO> getMenu(@PathVariable Long menuId) {
        Menu menu = menuService.getMenu(menuId);
        return ResponseEntity.ok(convertToDTO(menu));
    }

    private MenuDTO convertToDTO(Menu menu) {
        MenuDTO dto = new MenuDTO();
        dto.setName(menu.getName());
        dto.setPrice(menu.getPrice());
        dto.setDescription(menu.getDescription());
        dto.setIsAvailable(menu.getIsAvailable());
        return dto;
    }
}