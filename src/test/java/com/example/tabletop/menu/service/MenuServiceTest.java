package com.example.tabletop.menu.service;

import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.exception.MenuNotFoundException;
import com.example.tabletop.menu.repository.MenuRepository;
import com.example.tabletop.menuimage.entity.MenuImage;
import com.example.tabletop.menuimage.service.MenuImageService;
import com.example.tabletop.orderitem.repository.OrderitemRepository;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.exception.StoreNotFoundException;
import com.example.tabletop.store.repository.StoreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;

class MenuServiceTest {
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private MenuImageService imageService;
    @Mock
    private OrderitemRepository orderitemRepository;

    @InjectMocks
    private MenuService menuService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateMenu() throws Exception {
        // given
        Long storeId = 1L;
        String name = "Test Menu";
        Integer price = 10000;
        String description = "Test description";
        Boolean isAvailable = true;
        MultipartFile imageFile = mock(MultipartFile.class);
        Store store = Store.builder().storeId(storeId).build();
        Menu menu = Menu.builder()
                .name(name)
                .price(price)
                .description(description)
                .isAvailable(isAvailable)
                .store(store)
                .build();
        Menu savedMenu = Menu.builder()
                .id(1L)
                .name(name)
                .price(price)
                .description(description)
                .isAvailable(isAvailable)
                .store(store)
                .build();

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(menuRepository.save(any(Menu.class))).thenReturn(savedMenu);
        when(imageFile.isEmpty()).thenReturn(false);
        MenuImage menuImage = MenuImage.builder()
                .menuImageId(1L)
                .menuId(1L)
                .filename("uuid_filename.jpg")
                .fileOriginalName("original_filename.jpg")
                .filepath("/path/to/image")
                .S3Url("http://example.com/image.jpg")
                .build();
        when(imageService.saveImage(eq(imageFile), eq(1L))).thenReturn(menuImage);

        // when
        Menu result = menuService.createMenu(storeId, name, price, description, isAvailable, imageFile);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(name, result.getName());
        Assertions.assertEquals(price, result.getPrice());
        Assertions.assertEquals(description, result.getDescription());
        Assertions.assertEquals(isAvailable, result.getIsAvailable());
        Assertions.assertEquals("http://example.com/image.jpg", result.getMenuimageUrl());
        verify(menuRepository, times(1)).save(any(Menu.class));
        verify(imageService, times(1)).saveImage(eq(imageFile), eq(1L));
    }

    @Test
    void testUpdateMenu() throws Exception {
        // given
        Long storeId = 1L;
        Long menuId = 1L;
        String newName = "Updated Menu";
        Integer newPrice = 12000;
        String newDescription = "Updated description";
        Boolean newIsAvailable = false;
        MultipartFile newImageFile = mock(MultipartFile.class);

        Store store = Store.builder().storeId(storeId).build();
        Menu existingMenu = Menu.builder()
                .id(menuId)
                .name("Old Menu")
                .price(10000)
                .description("Old description")
                .isAvailable(true)
                .store(store)
                .build();
        Menu updatedMenu = Menu.builder()
                .id(menuId)
                .name(newName)
                .price(newPrice)
                .description(newDescription)
                .isAvailable(newIsAvailable)
                .store(store)
                .build();

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(existingMenu));
        when(menuRepository.save(any(Menu.class))).thenReturn(updatedMenu);
        when(newImageFile.isEmpty()).thenReturn(false);
        MenuImage newMenuImage = MenuImage.builder()
                .menuImageId(2L)
                .menuId(menuId)
                .filename("new_uuid_filename.jpg")
                .fileOriginalName("new_original_filename.jpg")
                .filepath("/path/to/new/image")
                .S3Url("http://example.com/new-image.jpg")
                .build();
        when(imageService.saveImage(eq(newImageFile), eq(menuId))).thenReturn(newMenuImage);

        // when
        Menu result = menuService.updateMenu(storeId, menuId, newName, newPrice, newDescription, newIsAvailable, newImageFile);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(newName, result.getName());
        Assertions.assertEquals(newPrice, result.getPrice());
        Assertions.assertEquals(newDescription, result.getDescription());
        Assertions.assertEquals(newIsAvailable, result.getIsAvailable());
        Assertions.assertEquals("http://example.com/new-image.jpg", result.getMenuimageUrl());
        verify(menuRepository, times(1)).findById(menuId);
        verify(menuRepository, times(1)).save(any(Menu.class));
        verify(imageService, times(1)).saveImage(eq(newImageFile), eq(menuId));
    }

    @Test
    void testDeleteMenu() throws IOException {
        // given
        Long menuId = 1L;
        Store store = Store.builder().storeId(1L).build();
        Menu menu = Menu.builder()
                .id(menuId)
                .name("Menu")
                .price(10000)
                .description("Description")
                .isAvailable(true)
                .store(store)
                .build();
        MenuImage menuImage = MenuImage.builder()
                .menuImageId(1L)
                .menuId(menuId)
                .filename("uuid_filename.jpg")
                .fileOriginalName("original_filename.jpg")
                .filepath("/path/to/image")
                .S3Url("http://example.com/image.jpg")
                .build();
        menu.setMenuImage(menuImage);

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        // when
        menuService.deleteMenu(menuId);

        // then
        verify(imageService, times(1)).deleteImage(eq(1L));
        verify(orderitemRepository, times(1)).nullifyMenuReference(menuId);
        verify(menuRepository, times(1)).delete(menu);
    }

    @Test
    void testCreateMenuWithInvalidStore() {
        // given
        Long invalidStoreId = 999L;
        when(storeRepository.findById(invalidStoreId)).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(StoreNotFoundException.class, () -> 
            menuService.createMenu(invalidStoreId, "Name", 10000, "Description", true, null)
        );
    }

    @Test
    void testUpdateNonExistentMenu() {
        // given
        Long nonExistentMenuId = 999L;
        when(menuRepository.findById(nonExistentMenuId)).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(MenuNotFoundException.class, () -> 
            menuService.updateMenu(1L, nonExistentMenuId, "Name", 10000, "Description", true, null)
        );
    }

    @Test
    void testGetMenuimageUrl() {
        // given
        Menu menu = Menu.builder().id(1L).name("Test Menu").build();
        MenuImage menuImage = MenuImage.builder()
                .menuImageId(1L)
                .menuId(1L)
                .filename("uuid_filename.jpg")
                .fileOriginalName("original_filename.jpg")
                .filepath("/path/to/image")
                .S3Url("http://example.com/image.jpg")
                .build();
        menu.setMenuImage(menuImage);

        // when
        String imageUrl = menu.getMenuimageUrl();

        // then
        Assertions.assertEquals("http://example.com/image.jpg", imageUrl);
    }

    @Test
    void testGetMenuimageUrlWithNoImage() {
        // given
        Menu menu = Menu.builder().id(1L).name("Test Menu").build();

        // when
        String imageUrl = menu.getMenuimageUrl();

        // then
        Assertions.assertNull(imageUrl);
    }
}