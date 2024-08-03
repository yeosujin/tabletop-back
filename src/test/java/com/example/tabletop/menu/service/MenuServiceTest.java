package com.example.tabletop.menu.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.repository.MenuRepository;
import com.example.tabletop.menu.service.MenuService;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.repository.StoreRepository;
import com.example.tabletop.menuimage.service.MenuImageService;

public class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private MenuImageService imageService;

    @InjectMocks
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateMenu() throws Exception {
        // Arrange
        Long storeId = 1L;
        Store store = new Store();
        store.setStoreId(storeId);
        
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("Test Menu");
        menu.setPrice(1000);
        menu.setDescription("Test Description");
        menu.setIsAvailable(true);
        menu.setStore(store);

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(menuRepository.save(any(Menu.class))).thenReturn(menu);

        // Act
        Menu createdMenu = menuService.createMenu(storeId, "Test Menu", 1000, "Test Description", true, null);

        // Assert
        assertNotNull(createdMenu);
        assertEquals("Test Menu", createdMenu.getName());
        assertEquals(1000, createdMenu.getPrice());
        assertEquals("Test Description", createdMenu.getDescription());
        assertTrue(createdMenu.getIsAvailable());
    }

    @Test
    void testGetMenu() {
        // Arrange
        Long menuId = 1L;
        Menu menu = new Menu();
        menu.setId(menuId);
        menu.setName("Test Menu");

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        // Act
        Menu foundMenu = menuService.getMenu(menuId);

        // Assert
        assertNotNull(foundMenu);
        assertEquals(menuId, foundMenu.getId());
        assertEquals("Test Menu", foundMenu.getName());
    }

    @Test
    void testUpdateMenu() throws Exception {
        // Arrange
        Long storeId = 1L;
        Long menuId = 1L;
        Menu existingMenu = new Menu();
        existingMenu.setId(menuId);
        existingMenu.setName("Old Name");
        existingMenu.setStore(new Store());
        existingMenu.getStore().setStoreId(storeId);

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(existingMenu));
        when(menuRepository.save(any(Menu.class))).thenReturn(existingMenu);

        // Act
        Menu updatedMenu = menuService.updateMenu(storeId, menuId, "New Name", 2000, "New Description", false, null);

        // Assert
        assertNotNull(updatedMenu);
        assertEquals("New Name", updatedMenu.getName());
        assertEquals(2000, updatedMenu.getPrice());
        assertEquals("New Description", updatedMenu.getDescription());
        assertFalse(updatedMenu.getIsAvailable());
    }

    @Test
    void testDeleteMenu() throws Exception {
        // Arrange
        Long storeId = 1L;
        Long menuId = 1L;
        Menu menu = new Menu();
        menu.setId(menuId);
        menu.setStore(new Store());
        menu.getStore().setStoreId(storeId);

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        // Act
        menuService.deleteMenu(storeId, menuId);

        // Assert
        verify(menuRepository, times(1)).delete(menu);
    }
}