package com.example.tabletop.store.repository;

import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.enums.StoreType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@DataJpaTest
class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @Test
    void testCRUD() {
        // Create
        Set<String> holidays = new HashSet<>(List.of("Monday", "Tuesday"));
        Store store = Store.builder()
                .name("Test Store")
                .storeType(StoreType.ORDINARY)
                .corporateRegistrationNumber("123456789")
                .openDate(LocalDate.now())
                .closeDate(LocalDate.now().plusDays(30))
                .description("Test store description")
                .address("123 Main St")
                .notice("Store notice")
                .openTime(LocalTime.of(9, 0))
                .closeTime(LocalTime.of(21, 0))
                .holidays(holidays)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Store savedStore = storeRepository.save(store);
        Assertions.assertNotNull(savedStore);

        // Read
        List<Store> stores = storeRepository.findAll();
        Assertions.assertNotNull(stores);
        Assertions.assertEquals(1, stores.size());
        Assertions.assertEquals(store.getName(), stores.get(0).getName());

        // Update
        savedStore.updateDetails("Updated Store", "Updated description", "Updated address", "Updated notice", LocalTime.of(10, 0), LocalTime.of(20, 0), new HashSet<>(List.of("Wednesday")));
        Store updatedStore = storeRepository.save(savedStore);
        Assertions.assertEquals("Updated Store", updatedStore.getName());
        Assertions.assertEquals("Updated description", updatedStore.getDescription());
        Assertions.assertEquals("Updated address", updatedStore.getAddress());
        Assertions.assertEquals("Updated notice", updatedStore.getNotice());
        Assertions.assertEquals(LocalTime.of(10, 0), updatedStore.getOpenTime());
        Assertions.assertEquals(LocalTime.of(20, 0), updatedStore.getCloseTime());
        Assertions.assertEquals(new HashSet<>(List.of("Wednesday")), updatedStore.getHolidays());

        // Delete
        storeRepository.deleteById(savedStore.getStoreId());
        Assertions.assertTrue(storeRepository.findById(savedStore.getStoreId()).isEmpty());
    }

    @Test
    void testFindAllBySeller_LoginId() {
        // given
        String sellerLoginId = "testSeller";
        Store store1 = Store.builder().name("Store 1").corporateRegistrationNumber("123456789").build();
        Store store2 = Store.builder().name("Store 2").corporateRegistrationNumber("987654321").build();
        // Assuming there's a method to set seller's login ID
        store1.setSellerLoginId(sellerLoginId);
        store2.setSellerLoginId(sellerLoginId);
        storeRepository.saveAll(List.of(store1, store2));

        // when
        List<Store> result = storeRepository.findAllBySeller_LoginId(sellerLoginId);

        // then
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.stream().allMatch(store -> store.getSellerLoginId().equals(sellerLoginId)));
    }

    @Test
    void testExistsByCorporateRegistrationNumber() {
        // given
        String corporateRegistrationNumber = "123456789";
        Store store = Store.builder()
                .name("Test Store")
                .corporateRegistrationNumber(corporateRegistrationNumber)
                .build();
        storeRepository.save(store);

        // when
        boolean exists = storeRepository.existsByCorporateRegistrationNumber(corporateRegistrationNumber);

        // then
        Assertions.assertTrue(exists);
    }

    @Test
    void testFindById() {
        // given
        Store store = Store.builder()
                .name("Test Store")
                .storeType(StoreType.ORDINARY)
                .corporateRegistrationNumber("123456789")
                .build();
        Store savedStore = storeRepository.save(store);

        // when
        Optional<Store> result = storeRepository.findById(savedStore.getStoreId());

        // then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(savedStore.getName(), result.get().getName());
    }
}