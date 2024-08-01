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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabletop.image.enums.ImageParentType;
import com.example.tabletop.image.service.S3ImageService;
import com.example.tabletop.menu.dto.MenuDTO;
import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.exception.MenuNotFoundException;
import com.example.tabletop.menu.service.MenuService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/stores/{storeId}/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;
    private final S3ImageService s3ImageService;

    // 메뉴 조회
    @GetMapping()
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MenuDTO> createMenu(
            @PathVariable Long storeId,
            @RequestPart("menuData") MenuDTO menuDTO,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            Menu newMenu = menuService.createMenu(
                    storeId,
                    menuDTO.getName(),
                    menuDTO.getPrice(),
                    menuDTO.getDescription(),
                    menuDTO.getIsAvailable()
            );

            // S3 image upload
            if (image != null) {
                s3ImageService.uploadS3File(image, newMenu.getId(), ImageParentType.MENU);
            }

            return new ResponseEntity<>(convertToDTO(newMenu), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 메뉴 수정
    @PutMapping(value = "/{menuId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMenu(
            @PathVariable Long menuId,
            @RequestPart("menuData") MenuDTO menuDTO,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            Menu updatedMenu = menuService.updateMenu(
                    menuDTO.getStoreId(),
                    menuId,
                    menuDTO.getName(),
                    menuDTO.getPrice(),
                    menuDTO.getDescription(),
                    menuDTO.getIsAvailable()
            );

            // S3 image upload
            if (image != null) {
                s3ImageService.uploadS3File(image, updatedMenu.getId(), ImageParentType.MENU);
            }

            return ResponseEntity.ok(convertToDTO(updatedMenu));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating menu: " + e.getMessage());
        }
    }

    // 메뉴 삭제
    @DeleteMapping("/{menuId}")
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

    @GetMapping("/{menuId}")
    public ResponseEntity<MenuDTO> getMenu(@PathVariable Long menuId) {
        Menu menu = menuService.getMenu(menuId);
        return ResponseEntity.ok(convertToDTO(menu));
    }

    private MenuDTO convertToDTO(Menu menu) {
        MenuDTO dto = new MenuDTO();
        dto.setId(menu.getId());
        dto.setStoreId(menu.getStore().getStoreId());
        dto.setName(menu.getName());
        dto.setPrice(menu.getPrice());
        dto.setDescription(menu.getDescription());
        dto.setIsAvailable(menu.getIsAvailable());
        return dto;
    }
}