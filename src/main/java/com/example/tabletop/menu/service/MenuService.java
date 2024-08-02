package com.example.tabletop.menu.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.exception.MenuNotFoundException;
import com.example.tabletop.menu.repository.MenuRepository;
import com.example.tabletop.menuimage.entity.MenuImage;
import com.example.tabletop.menuimage.exception.MenuImageProcessingException;
import com.example.tabletop.menuimage.service.MenuImageService;
import com.example.tabletop.orderitem.repository.OrderitemRepository;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.exception.StoreNotFoundException;
import com.example.tabletop.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final MenuImageService imageService;
    private final OrderitemRepository orderitemRepository;

    @Transactional(readOnly = true)
    public List<Menu> getMenusForInfiniteScroll(Long storeId, Long lastMenuId, int limit) {
        log.info("Fetching menus for store id: {}, last menu id: {}, limit: {}", storeId, lastMenuId, limit);
        Pageable pageable = PageRequest.of(0, limit);
        if (lastMenuId == null) {
            return menuRepository.findInitialMenusForInfiniteScroll(storeId, pageable);
        } else {
            return menuRepository.findMenusForInfiniteScroll(storeId, lastMenuId, pageable);
        }
    }

    @Transactional(readOnly = true)
    public List<Menu> getMenusByStoreId(Long storeId) {
        log.info("Fetching all menus for store id: {}", storeId);
        return menuRepository.findByStore_StoreId(storeId);
    }

    @Transactional
    public Menu createMenu(Long storeId, String name, Integer price, String description, Boolean isAvailable, MultipartFile imageFile) throws MenuImageProcessingException, Exception {
        log.info("Creating new menu for store id: {}", storeId);
        if (name == null || name.trim().isEmpty()) {
            log.error("Attempt to create menu with null or empty name");
            throw new IllegalArgumentException("Menu name cannot be null or empty");
        }
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> {
                    log.error("Store not found with id: {}", storeId);
                    return new StoreNotFoundException("Store not found with id: " + storeId);
                });

        Menu menu = Menu.builder()
                .name(name)
                .price(price)
                .description(description)
                .isAvailable(isAvailable)
                .store(store)
                .build();
        
        Long savedMenuId = menuRepository.save(menu).getId();
        System.out.println("menuid : " + savedMenuId);
        if (imageFile != null && !imageFile.isEmpty()) {
            MenuImage image = imageService.saveImage(imageFile, savedMenuId);
//            menu.setMenuImage(image);
        }
        
       
        log.info("Created new menu with id: {} for store id: {}", savedMenuId, storeId);
        return menu;
    }


    @Transactional
    public Menu updateMenu(Long storeId, Long menuId, String name, Integer price, String description, Boolean isAvailable, MultipartFile imageFile) throws MenuImageProcessingException, Exception {
        log.info("Updating menu with id: {} for menuImage id: {}", menuId);
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> {
                    log.error("Menu not found with id: {}", menuId);
                    return new MenuNotFoundException("Menu not found with id: " + menuId);
                });

        // 메뉴가 해당 스토어에 속하는지 확인
        if (!menu.getStore().getStoreId().equals(menuId)) {
            log.error("Menu with id: {} does not belong to store", menuId);
            throw new IllegalArgumentException("Menu does not belong to the specified store");
        }

        menu.setName(name);
        menu.setPrice(price);
        menu.setDescription(description);
        menu.setIsAvailable(isAvailable);

        if (imageFile != null && !imageFile.isEmpty()) {
            if (menu.getMenuImage() != null) {
                imageService.deleteImage(menu.getMenuImage().getMenuImageId());
            }
            MenuImage newMenuImage = imageService.saveImage(imageFile, menuId);
            menu.setMenuImage(newMenuImage);
        }

        Menu updatedMenu = menuRepository.save(menu);
        log.info("Updated menu with id: {} for store id: {}", menuId, menuId);
        return updatedMenu;
    }

    @Transactional
    public void deleteMenu(Long menuId) throws IOException {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuNotFoundException("Menu not found with id: " + menuId));

        // 메뉴가 해당 스토어에 속하는지 확인
        if (!menu.getStore().getStoreId().equals(menu.getStore().getStoreId())) {
            throw new IllegalArgumentException("Menu does not belong to the specified store");
        }

        // 이미지가 있다면 삭제
        if (menu.getMenuImage() != null) {
            imageService.deleteImage(menu.getMenuImage().getMenuImageId());
        }
        
        // order items 있다면 null 전환
        orderitemRepository.nullifyMenuReference(menu.getId());
        
        // 메뉴만 삭제
        menuRepository.delete(menu);

        log.info("Deleted menu with id: {}", menuId);
    }

    @Transactional(readOnly = true)
    public Menu getMenu(Long menuId) {
        log.info("Fetching menu with id: {}", menuId);
        return menuRepository.findById(menuId)
                .orElseThrow(() -> {
                    log.error("Menu not found with id: {}", menuId);
                    return new MenuNotFoundException("Menu not found with id: " + menuId);
                });
    }
}