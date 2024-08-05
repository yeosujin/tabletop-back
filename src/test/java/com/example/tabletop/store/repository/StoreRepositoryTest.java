package com.example.tabletop.store.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.tabletop.seller.entity.Seller;
import com.example.tabletop.seller.repository.SellerRepository;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.enums.StoreType;

@DataJpaTest
class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Test
    void testCRUD() {
        // Create
        Seller seller = Seller.builder()
                .loginId("testSeller")
                .username("Test Seller")
                .build();
        sellerRepository.save(seller);

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
                .seller(seller)
                .build();
        Store savedStore = storeRepository.save(store);
        assertNotNull(savedStore);

        // Read
        List<Store> stores = storeRepository.findAll();
        assertNotNull(stores);
        assertEquals(1, stores.size());
        assertEquals(store.getName(), stores.get(0).getName());

        // Update
        savedStore.updateDetails("Updated Store", "Updated description", "Updated address", "Updated notice", LocalTime.of(10, 0), LocalTime.of(20, 0), new HashSet<>(List.of("Wednesday")));
        Store updatedStore = storeRepository.save(savedStore);
        assertEquals("Updated Store", updatedStore.getName());
        assertEquals("Updated description", updatedStore.getDescription());
        assertEquals("Updated address", updatedStore.getAddress());
        assertEquals("Updated notice", updatedStore.getNotice());
        assertEquals(LocalTime.of(10, 0), updatedStore.getOpenTime());
        assertEquals(LocalTime.of(20, 0), updatedStore.getCloseTime());
        assertEquals(new HashSet<>(List.of("Wednesday")), updatedStore.getHolidays());

        // Delete
        storeRepository.deleteById(savedStore.getStoreId());
        assertTrue(storeRepository.findById(savedStore.getStoreId()).isEmpty());
    }

    @Test
    void testFindAllBySeller_LoginId() {
        // given
        Seller seller = Seller.builder()
                .loginId("testSeller")
                .username("Test Seller")
                .build();
        sellerRepository.save(seller);

        Store store1 = Store.builder()
                .name("Store 1")
                .corporateRegistrationNumber("123456789")
                .seller(seller)
                .build();
        Store store2 = Store.builder()
                .name("Store 2")
                .corporateRegistrationNumber("987654321")
                .seller(seller)
                .build();
        storeRepository.saveAll(List.of(store1, store2));

        // when
        List<Store> result = storeRepository.findAllBySeller_LoginId(seller.getLoginId());

        // then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(store -> store.getSeller().getLoginId().equals(seller.getLoginId())));
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
        assertTrue(exists);
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
        assertTrue(result.isPresent());
        assertEquals(savedStore.getName(), result.get().getName());
    }
}