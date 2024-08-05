package com.example.tabletop.store.service;

import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.repository.StoreRepository;
import com.example.tabletop.store.service.StoreService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService storeService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateStore() {
        // given
        String name = "Test Store";
        Store store = new Store(null, name);

        // when
        when(storeRepository.save(any(Store.class))).thenReturn(store);
        Store savedStore = storeService.createStore(name);

        // then
        Assertions.assertNotNull(savedStore);
        Assertions.assertEquals(name, savedStore.getName());
        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    void testGetAllStores() {
        // given
        List<Store> stores = new ArrayList<>();
        stores.add(new Store(1L, "Store 1"));
        stores.add(new Store(2L, "Store 2"));

        // when
        when(storeRepository.findAll()).thenReturn(stores);
        List<Store> result = storeService.getAllStores();

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(stores.size(), result.size());
        for (int i = 0; i < stores.size(); i++) {
            Assertions.assertEquals(stores.get(i).getId(), result.get(i).getId());
            Assertions.assertEquals(stores.get(i).getName(), result.get(i).getName());
        }
        verify(storeRepository, times(1)).findAll();
    }

    @Test
    void testGetStoreById() {
        // given
        Long storeId = 1L;
        Store store = new Store(storeId, "Test Store");

        // when
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        Store result = storeService.getStoreById(storeId);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(store.getId(), result.getId());
        Assertions.assertEquals(store.getName(), result.getName());
        verify(storeRepository, times(1)).findById(storeId);
    }

    @Test
    void testUpdateStore() {
        // given
        Long storeId = 1L;
        String newName = "Updated Store";
        Store existingStore = new Store(storeId, "Test Store");
        Store updatedStore = new Store(storeId, newName);

        // when
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(existingStore));
        when(storeRepository.save(any(Store.class))).thenReturn(updatedStore);
        Store result = storeService.updateStore(storeId, newName);

        // then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(newName, result.getName());
        verify(storeRepository, times(1)).findById(storeId);
        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    void testDeleteStore() {
        // given
        Long storeId = 1L;

        // when
        doNothing().when(storeRepository).deleteById(storeId);
        storeService.deleteStore(storeId);

        // then
        verify(storeRepository, times(1)).deleteById(storeId);
    }
}