package com.example.tabletop.store.service;

import com.example.tabletop.seller.entity.Seller;
import com.example.tabletop.seller.repository.SellerRepository;
import com.example.tabletop.store.dto.StoreDetailsDTO;
import com.example.tabletop.store.dto.StoreListResponseDTO;
import com.example.tabletop.store.entity.Store;
import com.example.tabletop.store.enums.StoreType;
import com.example.tabletop.store.repository.StoreRepository;
import com.example.tabletop.storeimage.entity.StoreImage;
import com.example.tabletop.storeimage.service.StoreImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private StoreImageService storeImageService;

    @InjectMocks
    private StoreService storeService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetStoreListByLoginId() {
        // given
    	String loginId = "testUser";
        Seller seller = Seller.builder().id(1L).loginId(loginId).build();
        List<Store> stores = Arrays.asList(
            Store.builder().storeId(1L).name("Store 1").storeType(StoreType.ORDINARY).seller(seller).build(),
            Store.builder().storeId(2L).name("Store 2").storeType(StoreType.TEMPORARY).seller(seller).build()
        );
        when(storeRepository.findAllBySeller_LoginId(loginId)).thenReturn(stores);

        // when
        List<StoreListResponseDTO> result = storeService.getStoreListByLoginId(loginId);

        // then
        assertEquals(2, result.size());
        assertEquals("Store 1", result.get(0).getName());
        assertEquals(StoreType.ORDINARY, result.get(0).getStoreType());
        assertEquals("Store 2", result.get(1).getName());
        assertEquals(StoreType.TEMPORARY, result.get(1).getStoreType());
    }

    @Test
    void testCheckCorporateRegistrationNumberDuplication() {
        // given
        String corporateRegistrationNumber = "123456789";
        when(storeRepository.existsByCorporateRegistrationNumber(corporateRegistrationNumber)).thenReturn(true);

        // when
        boolean result = storeService.checkCorporateRegistrationNumberDuplication(corporateRegistrationNumber);

        // then
        assertTrue(result);
    }

    @Test
    void testInsertStore() throws Exception {
        // given
        String loginId = "testUser";
        Seller seller = Seller.builder()
                .id(1L)
                .loginId(loginId)
                .username("Test User")
                .build();
        when(sellerRepository.findByLoginId(loginId)).thenReturn(Optional.of(seller));

        Store savedStore = Store.builder().storeId(1L).build();
        when(storeRepository.saveAndFlush(any(Store.class))).thenReturn(savedStore);

        MultipartFile imageFile = mock(MultipartFile.class);
        when(imageFile.isEmpty()).thenReturn(false);

        StoreImage storeImage = StoreImage.builder()
                .filename("uuid_filename.jpg")
                .fileOriginalName("original_filename.jpg")
                .filepath("/path/to/image")
                .S3Url("https://s3-url.com/image.jpg")
                .build();
        when(storeImageService.saveImage(eq(1L), any(MultipartFile.class))).thenReturn(storeImage);

        // when
        storeService.insertStore(loginId, "Test Store", "ORDINARY", "123456789", null, null,
                "Description", "Address", "Notice", "09:00", "18:00", 
                new String[]{"Monday"}, imageFile);

        // then
        verify(storeRepository).saveAndFlush(any(Store.class));
        verify(storeImageService).saveImage(eq(1L), any(MultipartFile.class));
    }

    @Test
    void testGetStoreDetails() {
        // given
        Long storeId = 1L;
        Store store = Store.builder()
                .storeId(storeId)
                .name("Test Store")
                .storeType(StoreType.ORDINARY)
                .build();
        StoreImage storeImage = StoreImage.builder()
                .filename("uuid_filename.jpg")
                .fileOriginalName("original_filename.jpg")
                .filepath("/path/to/image")
                .S3Url("https://s3-url.com/image.jpg")
                .build();
        store.setStoreImage(storeImage);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        // when
        StoreDetailsDTO result = storeService.getStoreDetails(storeId);

        // then
        assertNotNull(result);
        assertEquals("Test Store", result.getName());
        assertEquals(StoreType.ORDINARY, result.getStoreType());
        assertEquals("https://s3-url.com/image.jpg", result.getS3Url());
    }

    @Test
    void testUpdateStoreByStoreId() throws Exception {
        // given
        Long storeId = 1L;
        Store store = Store.builder()
                .storeId(storeId)
                .name("Old Store")
                .build();
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        MultipartFile imageFile = mock(MultipartFile.class);
        when(imageFile.isEmpty()).thenReturn(false);

        StoreImage storeImage = StoreImage.builder()
                .filename("new_uuid_filename.jpg")
                .fileOriginalName("new_original_filename.jpg")
                .filepath("/new/path/to/image")
                .S3Url("https://new-s3-url.com/image.jpg")
                .build();
        when(storeImageService.saveImage(eq(storeId), any(MultipartFile.class))).thenReturn(storeImage);

        // when
        storeService.updateStoreByStoreId(storeId, "New Store", "New Description", "New Address",
                "New Notice", "10:00", "19:00", new String[]{"Sunday"}, imageFile);

        // then
        verify(storeRepository).save(any(Store.class));
        verify(storeImageService).saveImage(eq(storeId), any(MultipartFile.class));
    }

    @Test
    void testDeleteStoreByStoreId() throws Exception {
        // given
        Long storeId = 1L;
        Store store = Store.builder().storeId(storeId).build();
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        // when
        storeService.deleteStoreByStoreId(storeId);

        // then
        verify(storeImageService).deleteFolderFromS3(storeId);
        verify(storeRepository).delete(store);
    }
}