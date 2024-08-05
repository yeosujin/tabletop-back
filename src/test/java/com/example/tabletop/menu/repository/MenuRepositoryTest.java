package com.example.tabletop.menu.repository;

import com.example.tabletop.menu.entity.Menu;
import com.example.tabletop.menu.repository.MenuRepository;
import com.example.tabletop.store.entity.Store;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
class MenuRepositoryTest {

    @Autowired
    private MenuRepository menuRepository;

    @Test
    void testCRUD() {
        // Create
        Store store = Store.builder()
                .name("Test Store")
                .build();
        Menu menu = Menu.builder()
                .name("Test Menu")
                .description("Test menu description")
                .price(10000)
                .isAvailable(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .store(store)
                .build();
        Menu savedMenu = menuRepository.save(menu);
        Assertions.assertNotNull(savedMenu);

        // Read
        List<Menu> menus = menuRepository.findByStore_StoreId(store.getStoreId());
        Assertions.assertNotNull(menus);
        Assertions.assertEquals(1, menus.size());
        Assertions.assertEquals(menu.getName(), menus.get(0).getName());

        // Update
        savedMenu.setName("Updated Menu");
        Menu updatedMenu = menuRepository.save(savedMenu);
        Assertions.assertEquals("Updated Menu", updatedMenu.getName());

        // Delete
        menuRepository.deleteById(savedMenu.getId());
        Assertions.assertTrue(menuRepository.findById(savedMenu.getId()).isEmpty());
    }

    @Test
    void testFindByIdAndStoreId() {
        // given
        Store store = Store.builder()
                .name("Test Store")
                .build();
        Menu menu = Menu.builder()
                .name("Test Menu")
                .store(store)
                .build();
        menuRepository.save(menu);

        // when
        Optional<Menu> result = menuRepository.findById(menu.getId());

        // then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(menu.getId(), result.get().getId());
        Assertions.assertEquals(store.getStoreId(), result.get().getStore().getStoreId());
    }
}