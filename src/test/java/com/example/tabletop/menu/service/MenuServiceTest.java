package com.example.tabletop.menu.service;

import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.repository.MenuRepository;
import com.example.tabletop.menu.service.MenuService;
import com.example.tabletop.store.entity.Store;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuService menuService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateMenu() {
        // given
        Long storeId = 1L;
        String name = "Test Menu";
        Integer price = 10000;
        String description = "Test description";
        Boolean available = true;
        MultipartFile image = null;
        
        // when
        when(menuRepository.save(any(Menu.class))).thenReturn(new Menu(storeId, name, price, description, available, image));
        Menu savedMenu = menuService.createMenu(storeId, name, price, description, available, image);

        // then
        Assertions.assertNotNull(savedMenu);
        Assertions.assertEquals(name, savedMenu.getName());
        Assertions.assertEquals(storeId, savedMenu.getStore().getStoreId());
        Assertions.assertEquals(price, savedMenu.getPrice());
        Assertions.assertEquals(description, savedMenu.getDescription());
        Assertions.assertEquals(available, savedMenu.getIsAvailable());
        verify(menuRepository, times(1)).save(any(Menu.class));
    }

    @Test
    void testUpdateMenu() {
        // given
        Long menuId = 1L;
        Long storeId = 1L;
        String newName = "Updated Menu";
        Integer newPrice = 12000;
        String newDescription = "Updated description";
        Boolean newAvailable = false;
        MultipartFile newImage = null;

        Menu existingMenu = new Menu(menuId, storeId, "Test Menu", 10000, "Description", true, null);
        Menu updatedMenu = new Menu(menuId, storeId, newName, newPrice, newDescription, newAvailable, newImage);

        // when
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(existingMenu));
        when(menuRepository.save(any(Menu.class))).thenReturn(updatedMenu);
        Menu result = menuService.updateMenu(menuId, newName, newPrice, newDescription, newAvailable, newImage);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(newName, result.getName());
        Assertions.assertEquals(newPrice, result.getPrice());
        Assertions.assertEquals(newDescription, result.getDescription());
        Assertions.assertEquals(newAvailable, result.getIsAvailable());
        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, times(1)).save(any(Menu.class));
    }

    @Test
    void testDeleteMenu() {
        // given
        Long menuId = 1L;

        // when
        doNothing().when(menuRepository).deleteById(menuId);
        menuService.deleteMenu(menuId);

        // then
        verify(menuRepository, times(1)).deleteById(menuId);
    }
}