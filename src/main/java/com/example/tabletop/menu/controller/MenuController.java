package com.example.tabletop.menu.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabletop.menu.dto.MenuDTO;
import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.exception.MenuNotFoundException;
import com.example.tabletop.menu.service.MenuService;

@RestController
@RequestMapping
public class MenuController {
    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // 메뉴 조회
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
    
    // 메뉴 등록 
    @PostMapping(value = "/api/stores/{storeId}/menus", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MenuDTO> createMenu(
            @PathVariable Long storeId,
            @RequestPart("name") String name,
            @RequestPart("price") Integer price,
            @RequestPart("description") String description,
            @RequestPart("isAvailable") Boolean isAvailable,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            Menu newMenu = menuService.createMenu(
                storeId, 
                name, 
                price, 
                description, 
                isAvailable, 
                image
            );
            return new ResponseEntity<>(convertToDTO(newMenu), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/api/stores/{storeId}/menus/{menuId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMenu(
            @PathVariable Long storeId,
            @PathVariable Long menuId,
            @RequestPart("name") String name,
            @RequestPart("price") Integer price,
            @RequestPart("description") String description,
            @RequestPart("isAvailable") Boolean isAvailable,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            Menu updatedMenu = menuService.updateMenu(
                storeId,
                menuId,
                name,
                price,
                description,
                isAvailable,
                image
            );
            return ResponseEntity.ok(convertToDTO(updatedMenu));
        } catch (MenuNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // 메ㅠ 삭제
    @DeleteMapping("/api/stores/{storeId}/menus/{menuId}")
    public ResponseEntity<?> deleteMenu(@PathVariable Long storeId, @PathVariable Long menuId) {
        try {
            menuService.deleteMenu(storeId, menuId);
            return ResponseEntity.noContent().build();
        } catch (MenuNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting menu: " + e.getMessage());
        }
    }

    @GetMapping("/api/stores/{storeId}/menus/{menuId}")
    public ResponseEntity<MenuDTO> getMenu(@PathVariable Long menuId) {
        Menu menu = menuService.getMenu(menuId);
        return ResponseEntity.ok(convertToDTO(menu));
    }

    private MenuDTO convertToDTO(Menu menu) {
        MenuDTO dto = new MenuDTO();
        dto.setId(menu.getId());
        dto.setName(menu.getName());
        dto.setPrice(menu.getPrice());
        dto.setDescription(menu.getDescription());
        dto.setIsAvailable(menu.getIsAvailable());
        
        if (menu.getImage() != null) {
            dto.setImagePath(menu.getImage().getFilepath());
        }
        
        return dto;
    }
}