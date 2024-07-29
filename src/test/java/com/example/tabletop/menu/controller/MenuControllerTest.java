package com.example.tabletop.menu.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.example.tabletop.image.enums.ImageParentType;
import com.example.tabletop.image.service.ImageService;
import com.example.tabletop.menu.dto.MenuDTO;
import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.repository.MenuRepository;
import com.example.tabletop.menu.service.MenuService;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.repository.StoreRepository;

import io.jsonwebtoken.io.IOException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MenuControllerTest {

    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuController menuController;

    private Menu testMenu;
    private MenuDTO testMenuDTO;

    @BeforeEach
    void setUp() {
        testMenu = Menu.builder()
        		.id(1L)
        		.name("Test Menu")
        		.price(1000)
        		.description("Test Description")
        		.isAvailable(true)
        		.store(null)
        		.build(); //new Menu(1L, "Test Menu", 1000, "Test Description", true, null);
        testMenuDTO = new MenuDTO();
        testMenuDTO.setId(1L);
        testMenuDTO.setName("Test Menu");
        testMenuDTO.setPrice(1000);
        testMenuDTO.setDescription("Test Description");
        testMenuDTO.setIsAvailable(true);
    }

    @Test
    void testGetMenus() {
        Long storeId = 1L;
        Long lastMenuId = null;
        int limit = 20;
        List<Menu> menus = Arrays.asList(testMenu);
        when(menuService.getMenusForInfiniteScroll(storeId, lastMenuId, limit)).thenReturn(menus);

        ResponseEntity<List<MenuDTO>> response = menuController.getMenus(storeId, lastMenuId, limit);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(testMenu.getId(), response.getBody().get(0).getId());
    }

    @Test
    void testCreateMenu() throws Exception {
        Long storeId = 1L;
        when(menuService.createMenu(eq(storeId), anyString(), anyInt(), anyString(), anyBoolean(), any())).thenReturn(testMenu);

        ResponseEntity<MenuDTO> response = menuController.createMenu(storeId, testMenuDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testMenu.getId(), response.getBody().getId());
    }

    @Test
    void testUpdateMenu() throws Exception {
        Long storeId = 1L;
        Long menuId = 1L;
        when(menuService.updateMenu(eq(storeId), eq(menuId), anyString(), anyInt(), anyString(), anyBoolean(), any())).thenReturn(testMenu);

        ResponseEntity<?> response = menuController.updateMenu(storeId, menuId, testMenuDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testMenu.getId(), ((MenuDTO)response.getBody()).getId());
    }

    @Test
    void testDeleteMenu() throws Exception {
        Long storeId = 1L;
        Long menuId = 1L;
        doNothing().when(menuService).deleteMenu(storeId, menuId);

        ResponseEntity<?> response = menuController.deleteMenu(storeId, menuId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetMenu() {
        Long menuId = 1L;
        when(menuService.getMenu(menuId)).thenReturn(testMenu);

        ResponseEntity<MenuDTO> response = menuController.getMenu(menuId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testMenu.getId(), response.getBody().getId());
    }
}
