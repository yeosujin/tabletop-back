package com.example.tabletop.menu.service;


import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabletop.image.entity.Image;
import com.example.tabletop.image.enums.ImageParentType;
import com.example.tabletop.image.service.ImageService;
import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.repository.MenuRepository;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.repository.StoreRepository;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final ImageService imageService;

    @Autowired
    public MenuService(MenuRepository menuRepository, StoreRepository storeRepository, ImageService imageService) {
        this.menuRepository = menuRepository;
        this.storeRepository = storeRepository;
        this.imageService = imageService;
    }

    @Transactional(readOnly = true)
    public List<Menu> getMenusForInfiniteScroll(Long storeId, Long lastMenuId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        if (lastMenuId == null) {
            return menuRepository.findInitialMenusForInfiniteScroll(storeId, pageable);
        } else {
            return menuRepository.findMenusForInfiniteScroll(storeId, lastMenuId, pageable);
        }
    }

    @Transactional(readOnly = true)
    public List<Menu> getMenusByStoreId(Long storeId) {
        return menuRepository.findByStore_StoreId(storeId);
    }

    @Transactional
    public Menu createMenu(Long storeId, String name, Integer price, String description, Boolean isAvailable, MultipartFile imageFile) throws IOException {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NoSuchElementException("Store not found with id: " + storeId));

        Menu menu = Menu.builder()
                .name(name)
                .price(price)
                .description(description)
                .isAvailable(isAvailable)
                .store(store)
                .build();

        if (imageFile != null && !imageFile.isEmpty()) {
            Image image = imageService.saveImage(imageFile, storeId, ImageParentType.MENU);
            menu.setImage(image);
        }

        return menuRepository.save(menu);
    }

    @Transactional
    public Menu updateMenu(Long menuId, String name, Integer price, String description, Boolean isAvailable, MultipartFile imageFile) throws IOException {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new NoSuchElementException("Menu not found with id: " + menuId));

        menu.setName(name);
        menu.setPrice(price);
        menu.setDescription(description);
        menu.setIsAvailable(isAvailable);

        if (imageFile != null && !imageFile.isEmpty()) {
            if (menu.getImage() != null) {
                imageService.deleteImage(menu.getImage().getImageId());
            }
            Image newImage = imageService.saveImage(imageFile, menu.getStore().getStoreId(), ImageParentType.MENU);
            menu.setImage(newImage);
        }

        return menuRepository.save(menu);
    }

    @Transactional
    public void deleteMenu(Long menuId) throws IOException {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new NoSuchElementException("Menu not found with id: " + menuId));

        if (menu.getImage() != null) {
            imageService.deleteImage(menu.getImage().getImageId());
        }

        menuRepository.delete(menu);
    }

    @Transactional(readOnly = true)
    public Menu getMenu(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new NoSuchElementException("Menu not found with id: " + menuId));
    }
}